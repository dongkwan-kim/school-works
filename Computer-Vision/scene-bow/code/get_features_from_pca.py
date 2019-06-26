import numpy as np
import numpy.linalg as npl


def get_features_from_pca(feat_num, feature):
    """
    This function loads 'vocab_sift.npy' or 'vocab_hog.npg' file and
    returns dimension-reduced vocab into 2D or 3D.

    :param feat_num: 2 when we want 2D plot, 3 when we want 3D plot
    :param feature: 'Hog' or 'SIFT'

    :return: an N x feat_num matrix
    """

    if feature == 'HoG':
        vocab = np.load('vocab_hog.npy')
    elif feature == 'SIFT':
        vocab = np.load('vocab_sift.npy')
    else:
        raise ValueError

    # Your code here. You should also change the return value.

    vocab = vocab - np.mean(vocab, axis=0)
    vocab_cov = np.cov(vocab.T)

    eigen_values, eigen_vectors = npl.eig(vocab_cov)

    dec_eigen_args = np.argsort(eigen_values)[::-1]
    dec_eigen_values, dec_eigen_vectors = eigen_values[dec_eigen_args], eigen_vectors[:, dec_eigen_args]

    vocab_pca = np.dot(vocab, dec_eigen_vectors[:, :feat_num])

    # return np.zeros((vocab.shape[0], 2))
    return vocab_pca
