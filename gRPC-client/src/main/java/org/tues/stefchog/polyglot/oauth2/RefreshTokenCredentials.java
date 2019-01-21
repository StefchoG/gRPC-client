package org.tues.stefchog.polyglot.oauth2;

import java.io.IOException;
import java.time.Clock;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.common.annotations.VisibleForTesting;

import org.tues.stefchog.polyglot.ConfigProto.OauthConfiguration.OauthClient;

/**
 * Represents a refresh token in a specific oauth2 ecosystem. Swaps the refresh token for an access
 * token if the access token expires.
 */
public class RefreshTokenCredentials extends OAuth2Credentials {
  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCredentials.class);

  /**
   * A factor applied to the access token lifetime to make sure we refresh the token a bit earlier
   * than it actually expires.
   */
  private static final double ACCESS_TOKEN_EXPIRY_MARGIN = 0.8;

  private final String refreshTokenSecret;
  private final OauthClient oauthClient;
  private final String tokenExchangeUrl;
  private final Clock clock;
  private final RefreshRequestFactory requestFactory;

  /** Create a new set of credentials for the given refresh token and oauth configuration. */
  public static RefreshTokenCredentials create(
      OauthClient oauthConfig, String refreshTokenSecret, String tokenExchangeUrl) {
    RefreshRequestFactory requestFactory = new RefreshRequestFactory();
    Clock clock = Clock.systemDefaultZone();
    return new RefreshTokenCredentials(
        requestFactory, refreshTokenSecret, tokenExchangeUrl, oauthConfig, clock);
  }

  @VisibleForTesting
  RefreshTokenCredentials(
      RefreshRequestFactory requestFactory,
      String refreshTokenSecret,
      String tokenExchangeUrl,
      OauthClient oauthClient,
      Clock clock) {
    this.requestFactory = requestFactory;
    this.refreshTokenSecret = refreshTokenSecret;
    this.oauthClient = oauthClient;
    this.tokenExchangeUrl = tokenExchangeUrl;
    this.clock = clock;
  }

  @Override
  public AccessToken refreshAccessToken() throws IOException {
    logger.info("Exchanging refresh token for access token");
    RefreshTokenRequest refreshRequest = requestFactory.newRequest(
        oauthClient, refreshTokenSecret, tokenExchangeUrl);
    TokenResponse refreshResponse = refreshRequest.execute();

    logger.info("Refresh successful, got access token");
    return new AccessToken(
        refreshResponse.getAccessToken(),
        computeExpirtyDate(refreshResponse.getExpiresInSeconds()));
  }

  private Date computeExpirtyDate(long expiresInSeconds) {
    long expiresInSecondsWithMargin = (long) (expiresInSeconds * ACCESS_TOKEN_EXPIRY_MARGIN);
    return Date.from(clock.instant().plusSeconds(expiresInSecondsWithMargin));
  }

  @VisibleForTesting
  static class RefreshRequestFactory {
    RefreshTokenRequest newRequest(
        OauthClient oauthClient, String refreshTokenSecret, String tokenEndpoint) {
      RefreshTokenRequest result = new RefreshTokenRequest(
          new NetHttpTransport(),
          new JacksonFactory(),
          new GenericUrl(tokenEndpoint),
          refreshTokenSecret);
      result.setClientAuthentication(
          new BasicAuthentication(oauthClient.getId(), oauthClient.getSecret()));
      return result;
    }
  }
}
