import tensorflow as tf
import numpy as np
import os

tf.logging.set_verbosity(tf.logging.INFO)

DEPTH = 7


def dropout(inputs, training, drop_prob=0.4):
    return tf.layers.dropout(inputs=inputs, rate=drop_prob, training=training)


def fc_layer(inputs, units, training, activation=tf.nn.relu):
    return dropout(tf.layers.dense(inputs=inputs, units=units, activation=activation), training=training)


def output_layer(inputs, training):
    return fc_layer(inputs=inputs, units=10, training=training, activation=None)


def custom_model_fn(features, labels, mode):
    """Model function for PA2"""

    learning_rate = 0.001

    training = True if mode == tf.estimator.ModeKeys.TRAIN else False

    def _dropout(_inputs, _drop_prob=0.4, _training=training):
        return dropout(_inputs, _training, _drop_prob)

    # Write your custom layer
    # Input Layer
    inputs = tf.reshape(features["x"], [-1, 32, 32, 3])

    # Output logits Layer
    if DEPTH == 3:
        conv_1 = tf.layers.conv2d(inputs, filters=32, kernel_size=5, padding='same', activation=tf.nn.relu)
        pool_1 = _dropout(tf.layers.max_pooling2d(conv_1, pool_size=2, strides=2))

        fc_1 = fc_layer(tf.contrib.layers.flatten(pool_1), 1024, training)
        logits = output_layer(fc_1, training)
    elif DEPTH == 5:
        conv_1 = tf.layers.conv2d(inputs, filters=32, kernel_size=5, padding='same', activation=tf.nn.relu)
        pool_1 = _dropout(tf.layers.max_pooling2d(conv_1, pool_size=2, strides=2))

        conv_2 = tf.layers.conv2d(pool_1, filters=64, kernel_size=3, padding='same', activation=tf.nn.relu)
        conv_3 = tf.layers.conv2d(conv_2, filters=64, kernel_size=3, padding='same', activation=tf.nn.relu)
        pool_2 = _dropout(tf.layers.max_pooling2d(conv_3, pool_size=2, strides=2))

        fc_1 = fc_layer(tf.contrib.layers.flatten(pool_2), 1024, training)
        logits = output_layer(fc_1, training)
    elif DEPTH == 7:
        conv_1 = tf.layers.conv2d(inputs, filters=32, kernel_size=5, padding='same', activation=tf.nn.relu)
        pool_1 = _dropout(tf.layers.max_pooling2d(conv_1, pool_size=2, strides=2))

        conv_2 = tf.layers.conv2d(pool_1, filters=64, kernel_size=3, padding='same', activation=tf.nn.relu)
        conv_3 = tf.layers.conv2d(conv_2, filters=64, kernel_size=3, padding='same', activation=tf.nn.relu)
        pool_2 = _dropout(tf.layers.max_pooling2d(conv_3, pool_size=2, strides=2))

        conv_4 = tf.layers.conv2d(pool_2, filters=128, kernel_size=2, padding='same', activation=tf.nn.relu)
        conv_5 = tf.layers.conv2d(conv_4, filters=128, kernel_size=2, padding='same', activation=tf.nn.relu)
        pool_3 = _dropout(tf.layers.max_pooling2d(conv_5, pool_size=2, strides=2))

        fc_1 = fc_layer(tf.contrib.layers.flatten(pool_3), 1024, training)
        logits = output_layer(fc_1, training)
    else:
        raise Exception('Unexpected number')

    predictions = {
        # Generate predictions (for PREDICT and EVAL mode)
        "classes": tf.argmax(input=logits, axis=1),
        # Add `softmax_tensor` to the graph. It is used for PREDICT and by the
        # `logging_hook`.
        "probabilities": tf.nn.softmax(logits, name="softmax_tensor")
    }

    # In predictions, return the prediction value, do not modify
    if mode == tf.estimator.ModeKeys.PREDICT:
        return tf.estimator.EstimatorSpec(mode=mode, predictions=predictions)

    # Select your loss and optimizer from tensorflow API
    # Calculate Loss (for both TRAIN and EVAL modes)
    loss = tf.losses.sparse_softmax_cross_entropy(labels=labels, logits=logits)

    # Configure the Training Op (for TRAIN mode)
    if mode == tf.estimator.ModeKeys.TRAIN:
        optimizer = tf.train.AdamOptimizer(learning_rate)
        train_op = optimizer.minimize(loss=loss, global_step=tf.train.get_global_step())
        return tf.estimator.EstimatorSpec(mode=mode, loss=loss, train_op=train_op)

    # Add evaluation metrics (for EVAL mode)
    eval_metric_ops = {"accuracy": tf.metrics.accuracy(labels=labels, predictions=predictions["classes"])}
    return tf.estimator.EstimatorSpec(mode=mode, loss=loss, eval_metric_ops=eval_metric_ops)


if __name__ == '__main__':

    ONLY_EVAL = False
    PATH = 'PA2_extra2'

    total_steps = 15000
    interval = 1000

    # Write your dataset path
    dataset_train = np.load(os.path.join(PATH, 'extra2-train.npy'))
    dataset_eval = np.load(os.path.join(PATH, 'extra2-valid.npy'))
    test_data = np.load(os.path.join(PATH, 'extra2-test_img.npy'))

    f_dim = dataset_train.shape[1] - 1
    train_data = dataset_train[:, :f_dim].astype(np.float32)
    train_labels = dataset_train[:, f_dim].astype(np.int32)
    eval_data = dataset_eval[:, :f_dim].astype(np.float32)
    eval_labels = dataset_eval[:, f_dim].astype(np.int32)
    test_data = test_data.astype(np.float32)

    # Save model and checkpoint
    # $ tensorboard --logdir=model_{0}
    mnist_classifier = tf.estimator.Estimator(
        model_fn=custom_model_fn,
        model_dir="./model_{0}/".format(DEPTH),
    )

    # Set up logging for predictions
    tensors_to_log = {"probabilities": "softmax_tensor"}
    logging_hook = tf.train.LoggingTensorHook(tensors=tensors_to_log, every_n_iter=50)

    (steps_argmax_accuracy, max_accuracy) = (-1, -1)
    for eval_no in range(1, int(total_steps / interval) + 1):

        # Train the model. You can train your model with specific batch size and epoches
        if not ONLY_EVAL:
            train_input = tf.estimator.inputs.numpy_input_fn(
                x={"x": train_data},
                y=train_labels,
                batch_size=100,
                num_epochs=None,
                shuffle=True,
            )
            mnist_classifier.train(input_fn=train_input, steps=interval, hooks=[logging_hook])

        # Eval the model. You can evaluate your trained model with validation data
        eval_input = tf.estimator.inputs.numpy_input_fn(x={"x": eval_data},
                                                        y=eval_labels, num_epochs=1, shuffle=False)
        eval_results = mnist_classifier.evaluate(input_fn=eval_input)
        (steps_argmax_accuracy, max_accuracy, changed) = (eval_results['global_step'], eval_results['accuracy'], True) \
            if eval_results['accuracy'] > max_accuracy else (steps_argmax_accuracy, max_accuracy, False)
        print(eval_results)

        # Predict the test dataset for steps that maximize the eval_accuracy
        # Do not modify!!!
        if changed:
            pred_input = tf.estimator.inputs.numpy_input_fn(x={"x": test_data}, shuffle=False)
            pred_results = mnist_classifier.predict(input_fn=pred_input)
            pred_list = list(pred_results)
            result = np.asarray([list(x.values())[0] for x in pred_list])

            print('Saved {0} steps (acc: {1})'.format(steps_argmax_accuracy, max_accuracy))
            np.save('extra_20183060_network_{0}.npy'.format(DEPTH), result)
