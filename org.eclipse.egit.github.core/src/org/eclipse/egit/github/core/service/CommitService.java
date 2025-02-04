/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMMENTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMMITS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMPARE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

import com.google.gson.reflect.TypeToken;

/**
 * Service for interacting with repository commits
 *
 * @see <a href="http://developer.github.com/v3/repos/commits">GitHub commit API
 *      documentation</a>
 */
public class CommitService extends GitHubService {

	/**
	 * Create commit service
	 */
	public CommitService() {
		super();
	}

	/**
	 * Create commit service
	 *
	 * @param client
	 */
	public CommitService(IGitHubClient client) {
		super(client);
	}

	/**
	 * Get all commits in given repository
	 *
	 * @param repository
	 * @return non-null but possibly empty list of repository commits
	 * @throws IOException
	 */
	public List<RepositoryCommit> getCommits(IRepositoryIdProvider repository)
			throws IOException {
		return getCommits(repository, null, null);
	}

	/**
	 * Get all commits in given repository beginning at an optional commit SHA-1
	 * and affecting an optional path.
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @return non-null but possibly empty list of repository commits
	 * @throws IOException
	 */
	public List<RepositoryCommit> getCommits(IRepositoryIdProvider repository,
			String sha, String path) throws IOException {
		return getAll(pageCommits(repository, sha, path));
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository) {
		return pageCommits(repository, null, null);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param size
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, int size) {
		return pageCommits(repository, null, null, size);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, String sha, String path) {
		return pageCommits(repository, sha, path, PAGE_SIZE);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @param size
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, String sha, String path, int size) {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		PagedRequest<RepositoryCommit> request = createPagedRequest(PAGE_FIRST,
				size);
		request.setUri(uri);
		request.setType(new TypeToken<List<RepositoryCommit>>() {
		}.getType());

		if (sha != null || path != null) {
			Map<String, String> params = new HashMap<String, String>();
			if (sha != null)
				params.put("sha", sha); //$NON-NLS-1$
			if (path != null)
				params.put("path", path); //$NON-NLS-1$
			request.setParams(params);
		}

		return createPageIterator(request);
	}

	/**
	 * Get commit with given SHA-1 from given repository
	 *
	 * @param repository
	 * @param sha
	 * @return repository commit
	 * @throws IOException
	 */
	public RepositoryCommit getCommit(IRepositoryIdProvider repository,
			String sha) throws IOException {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(RepositoryCommit.class);
		return (RepositoryCommit) client.get(request).getBody();
	}

	/**
	 * Get all comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @return non-null but possibly empty list of commits
	 * @throws IOException
	 */
	public List<CommitComment> getComments(IRepositoryIdProvider repository,
			String sha) throws IOException {
		return getAll(pageComments(repository, sha));
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha) {
		return pageComments(repository, sha, PAGE_SIZE);
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @param size
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha, int size) {
		return pageComments(repository, sha, PAGE_FIRST, size);
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @param start
	 * @param size
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha, int start, int size) {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		uri.append(SEGMENT_COMMENTS);
		PagedRequest<CommitComment> request = createPagedRequest(start, size);
		request.setUri(uri);
		request.setType(new TypeToken<List<CommitComment>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Get commit comment with given id
	 *
	 * @param repository
	 * @param commentId
	 * @return commit comment
	 * @throws IOException
	 */
	public CommitComment getComment(IRepositoryIdProvider repository,
			long commentId) throws IOException {
		String repoId = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(CommitComment.class);
		return (CommitComment) client.get(request).getBody();
	}

	/**
	 * Add comment to given commit
	 *
	 * @param repository
	 * @param sha
	 * @param comment
	 * @return created comment
	 * @throws IOException
	 */
	public CommitComment addComment(IRepositoryIdProvider repository,
			String sha, CommitComment comment) throws IOException {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		uri.append(SEGMENT_COMMENTS);
		return client.post(uri.toString(), comment, CommitComment.class);
	}

	/**
	 * Edit given comment
	 *
	 * @param repository
	 * @param comment
	 * @return edited comment
	 * @throws IOException
	 */
	public CommitComment editComment(IRepositoryIdProvider repository,
			CommitComment comment) throws IOException {
		String id = getId(repository);
		if (comment == null)
			throw new IllegalArgumentException("Comment cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(comment.getId());
		return client.post(uri.toString(), comment, CommitComment.class);
	}

	/**
	 * Delete commit comment with given id from given repository
	 *
	 * @param repository
	 * @param commentId
	 * @throws IOException
	 */
	public void deleteComment(IRepositoryIdProvider repository, long commentId)
			throws IOException {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		client.delete(uri.toString());
	}

	/**
	 * Compare base and head commits
	 *
	 * @param repository
	 * @param base
	 * @param head
	 * @return commit compare
	 * @throws IOException
	 */
	public RepositoryCommitCompare compare(IRepositoryIdProvider repository,
			String base, String head) throws IOException {
		String id = getId(repository);
		if (base == null)
			throw new IllegalArgumentException("Base cannot be null"); //$NON-NLS-1$
		if (base.length() == 0)
			throw new IllegalArgumentException("Base cannot be empty"); //$NON-NLS-1$

		if (head == null)
			throw new IllegalArgumentException("Head cannot be null"); //$NON-NLS-1$
		if (head.length() == 0)
			throw new IllegalArgumentException("Head cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMPARE);
		uri.append('/').append(base).append("...").append(head); //$NON-NLS-1$
		GitHubRequest request = createRequest();
		request.setType(RepositoryCommitCompare.class);
		request.setUri(uri);
		return (RepositoryCommitCompare) client.get(request).getBody();
	}
}
