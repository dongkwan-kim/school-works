from copy import deepcopy

import numpy as np
import numpy.linalg as npl


def eud(x, y):
    return npl.norm(x - y, axis=1)


def get_random_centers(all_features, vocab_size) -> np.ndarray:
    min_x = np.min(all_features)
    max_x = np.max(all_features)
    n_features = all_features.shape[-1]
    random_centers = (max_x - min_x) * np.random.random((vocab_size, n_features)) + min_x
    return random_centers


def sample_centers(all_features, vocab_size) -> np.ndarray:
    indices = np.random.permutation(len(all_features))
    return all_features[indices[:vocab_size]]


def kmeans_clustering(all_features, vocab_size, epsilon, max_iter):
    """
    The function kmeans implements a k-means algorithm that finds the centers of vocab_size clusters
    and groups the all_features around the clusters. As an output, centroids contains a
    center of the each cluster.

    :param all_features: an N x d matrix, where d is the dimensionality of the feature representation.
    :param vocab_size: number of clusters.
    :param epsilon: When the maximum distance between previous and current centroid is less than epsilon,
        stop the iteration.
    :param max_iter: maximum iteration of the k-means algorithm.

    :return: an vocab_size x d array, where each entry is a center of the cluster.
    """

    # Your code here. You should also change the return value.

    """
    from sklearn.cluster import KMeans
    kmeans = KMeans(n_clusters=vocab_size, max_iter=max_iter, random_state=0)
    return kmeans.fit_transform(all_features)
    """

    N, F = all_features.shape

    centers = sample_centers(all_features, vocab_size)
    clusters = np.zeros(N)

    for _ in range(max_iter):

        for i in range(N):
            dists = eud(centers, all_features[i])
            clusters[i] = int(np.argmin(dists))

        prev_centers = deepcopy(centers)
        for v in range(vocab_size):
            arg_v, = np.where(clusters == v)
            features_v = all_features[arg_v]
            centers[v] = np.mean(features_v, axis=0)

        max_dist_prev_curr = np.max(eud(centers, prev_centers))
        if max_dist_prev_curr < epsilon:
            break

        print(_, max_dist_prev_curr, np.any(np.isnan(centers)), np.all(np.isnan(centers)))

    assert (vocab_size, all_features.shape[1]) == centers.shape
    return centers


if __name__ == '__main__':
    test_kmeans = kmeans_clustering(
        np.asarray([
            [1, 2], [1, 4], [1, 0],
            [10, 2], [10, 4], [10, 0]
        ]),
        2,
        1e-5,
        100,
    )
    # [[10  2] [ 1  2]]
    print(test_kmeans)

    test_kmeans = kmeans_clustering(
        np.asarray([
            [1, 1], [6, 6], [7, 7],
            [3, 2], [6, 8], [2, 1], [2, 3]
        ]),
        2,
        1e-5,
        100,
    )
    # [[2 1] [6 7]]
    print(test_kmeans)
