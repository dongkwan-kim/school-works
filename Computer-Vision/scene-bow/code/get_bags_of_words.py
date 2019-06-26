import cv2
import numpy as np
import numpy.linalg as npl
from numpy import linalg

from distance import pdist
from feature_extraction import feature_extraction


def get_bags_of_words(image_paths, feature):
    """
    This function assumes that 'vocab.mat' exists and contains an N x feature vector
    length matrix 'vocab' where each row is a kmeans centroid or visual word. This
    matrix is saved to disk rather than passed in a parameter to avoid recomputing
    the vocabulary every run.

    :param image_paths: a N array of string where each string is an image path
    :param feature: name of image feature representation.

    :return: an N x d matrix, where d is the dimensionality of the
        feature representation. In this case, d will equal the number
        of clusters or equivalently the number of entries in each
        image's histogram ('vocab_size') below.
    """
    if feature == 'HoG':
        vocab = np.load('vocab_hog.npy')
    elif feature == 'SIFT':
        vocab = np.load('vocab_sift.npy')
    else:
        raise ValueError

    # Your code here. You should also change the return value.
    list_of_hist = []
    for idx, path in enumerate(image_paths):
        img = cv2.imread(path)[:, :, ::-1]

        hist = img_to_hist(img, feature, vocab)
        hist = hist / npl.norm(hist) if np.sum(hist) != 0 else hist
        list_of_hist.append(hist)

        if idx % 150 == 0:
            print("get_bags_of_words: {}% ({}/{})".format(
                100 * round(idx / len(image_paths), 3), idx, len(image_paths)))

    bow_features = np.vstack(list_of_hist)

    # return np.zeros((1500, 36))
    return bow_features


def img_to_hist(img, feature, vocab):
    vocab_size = vocab.shape[0]
    features = feature_extraction(img, feature)

    if features is not None:
        # (n_features, n_vocab)
        pairwise_distances = pdist(features, vocab)

        # (n_features,), 0 <= x < n_vocab
        clusters_of_features = np.argmin(pairwise_distances, axis=1)

        hist, _ = np.histogram(clusters_of_features, list(range(vocab_size + 1)))
    else:
        hist = np.zeros((200,))

    return hist
