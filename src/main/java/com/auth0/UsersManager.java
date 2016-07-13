package com.auth0;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.util.function.BiFunction;


public class UsersManager {

    public boolean getUsers(final String managementToken, final String connection, final String email, final String domain) {
        final BiFunction<String, String, String> getUri = (auth0Domain, path) -> String.format("https://%s%s", auth0Domain, path);
        if (email == null) {
            throw new IllegalStateException("Error Auth0");
        }
        try {
            final StringBuilder pathBuilder = new StringBuilder("/api/v2/users?");
            pathBuilder
                    .append("q=").append(URLEncoder.encode("email:", "UTF-8")).append("\"").append(email).append("\"")
                    .append(" AND ").append(URLEncoder.encode("identities.connection:", "UTF-8")).append("\"").append(connection).append("\"")
                    .append("&search_engine=v2");
            final String path = pathBuilder.toString();
            final String url = getUri.apply(domain, path);
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("authorization", "Bearer " + managementToken)
                    .addHeader("cache-control", "no-cache")
                    .build();

            final Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                // TODO - improve error handling
                throw new IllegalStateException("Error occurred searching for user by email: " + email);
            }
            final String result = response.body().string();
            // assume something other than empty array indicates results
            return !"[]".equals(result);

        } catch (Exception e) {
            throw new IllegalStateException("Error checking database info for user: ", e);
        }
    }

}
