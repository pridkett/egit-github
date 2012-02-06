package org.eclipse.egit.github.core.client;

import java.io.IOException;
import java.lang.reflect.Type;

public interface IGitHubClient {
	public GitHubResponse get(GitHubRequest request) throws IOException;

	public void post(String uri) throws IOException;

	public <V> V post(final String uri, final Object params, final Type type)
			throws IOException;

	public void put(String uri) throws IOException;

	public <V> V put(final String uri, final Object params, final Type type)
			throws IOException;

	public void delete(String uri) throws IOException;

	public void delete(final String uri, final Object params)
			throws IOException;

	public int getRateLimit();

	public int getRateLimitRemaining();
}
