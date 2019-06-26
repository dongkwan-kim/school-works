function [cameraParams] = estimateSingleCameraParameters(imagePoints, boardSize, patchSize, imageSize)
% This function will estimate camera parameters (intrinsic, extrinsic) from
% checkerboard image points.

% Zhang's method consists of 5 parts
% 1. Estimate homography from checkerboard plane to screen space.
% 2. Calculate B matrix by solving Vb = 0.
% 3. Extract intrinsic parameters from B matrix.
% 4. Calculate extrinsic parameters from intrinsic parameters and homography.
% 5. Refine parameters using the maximum likelihood estimation.

% Function inputs:
% - 'imagePoints': positions of checkerboard points in a screen space.
% - 'boardSize': the number of horizontal, vertical patchs in the checkerboard.
% - 'patchSize': the size of the checkerboard patch in mm.
% - 'imageSize': the size of the checkerboard image in pixels.

% Function outputs:
% - 'cameraParams': a camera parameter includes intrinsic and extrinsic.

numView = size(imagePoints, 3);
numVerticalPatch = boardSize(1) - 1;
numHorizontalPatch = boardSize(2) - 1;
numCorner = size(imagePoints, 1);

%% Estimate a homography (appendix A)
% Generate checkerboard world points
worldPoints = zeros(size(imagePoints,1), size(imagePoints,2));

% Fill worldPoints (positions of checkerboard corners)
% ----- Your code here (1) ----- (slide 6): Done
for i = 1:numHorizontalPatch
    for j = 1:numVerticalPatch
        worldPoints(numVerticalPatch * (i - 1) + j, 1) = (i - 1) * patchSize;
        worldPoints(numVerticalPatch * (i - 1) + j, 2) = (j - 1) * patchSize;
    end
end

% Build L matrix
% 108     9     6
L = zeros(2 * numCorner, 9, numView);

% Fill L matrix
% ----- Your code here (2) ----- (slide 13): Done with question
num_coords = 2 * numCorner;

% u, v: imagePoints
% X, Y: worldPoints
for nv = 1:numView
    u = imagePoints(:, 1, nv);
    v = imagePoints(:, 2, nv);
    L(1:2:num_coords, 1:3, nv) = - [worldPoints ones(numCorner, 1)];  % -X -Y -1
    L(1:2:num_coords, 7:9, nv) = [u .* worldPoints u];  % uX uY u
    L(2:2:num_coords, 4:6, nv) = - [worldPoints ones(numCorner, 1)];  % -X -Y -1
    L(2:2:num_coords, 7:9, nv) = [v .* worldPoints v];  % vX vY v
end

% Calculate a homography using SVD
% 3 3 6
homography = zeros(3, 3, numView);

% Fill homography matrix
% ----- Your code here (3) ----- (slide 15): Done
for nv = 1:numView
    [~, ~, vL] = svd(L(:, :, nv));
    homography(:, :, nv) = reshape(vL(:, 9), 3, 3)';
end

%% Solve closed-form (section 3.1)
V = zeros(2 * numView, 6);
b = zeros(6, 1);

% Fill V matrix and calculate b vector
% ----- Your code here (4) ----- (slide 19, 23): Done

v_kl = @(h, k, l) [h(1, k) * h(1, l)
                   h(1, k) * h(2, l) + h(2, k) * h(1, l)
                   h(1, k) * h(3, l) + h(3, k) * h(1, l)
                   h(2, k) * h(2, l)
                   h(2, k) * h(3, l) + h(3, k) * h(2, l)
                   h(3, k) * h(3, l)];

for nv = 1:numView
    h_nv = homography(:, :, nv);
    V(2 * nv - 1, :) = v_kl(h_nv, 1, 2);
    V(2 * nv, :) = v_kl(h_nv, 1, 1) - v_kl(h_nv, 2, 2);
end

[~, ~, vV] = svd(V);
b = vV(:, 6);


%% Extraction of the intrinsic parameters from matrix B (appendix B)

% ----- Your code here (5) ----- (slide 24): Done
v0 = (b(2) * b(3) - b(1) * b(5)) / (b(1) * b(4) - b(2)^2);
lambda = b(6) - (b(3)^2 + v0 * (b(2) * b(3) - b(1) * b(5))) / b(1);
alpha = sqrt(lambda / b(1));
beta = sqrt(lambda * b(1) / (b(1) * b(4) - b(2)^2));
gamma = - b(2) * alpha^2 * beta / lambda;
u0 = gamma * v0 / beta - b(3) * alpha^2 / lambda;


%% Estimate initial RT (section 3.1)
Rt = zeros(3, 4, numView);

% Fill Rt matrix
% ----- Your code here (6) ----- (slide 25, 26): Done

K = [alpha gamma u0; 0 beta v0; 0 0 1];

for nv = 1:numView
    h1 = homography(:, 1, nv);
    h2 = homography(:, 2, nv);
    h3 = homography(:, 3, nv);
    
    lambda_prime = (1 / norm(K \ h1) + 1 / norm(K \ h2)) / 2;
    
    r1 = lambda_prime * K \ h1;
    r2 = lambda_prime * K \ h2;
    r3 = cross(r1, r2);
    R = [r1 r2 r3];
    
    [uR, ~, vR] = svd(R);
    R = uR * vR';
    
    t = lambda_prime * (K \ h3);
    
    Rt(:, :, nv) = [R t];
end

%% Maximum likelihood estimation (section 3.2)
options = optimoptions(@lsqnonlin, 'Algorithm', 'levenberg-marquardt', ...
    'TolX', 1e-32, 'TolFun', 1e-32, 'MaxFunEvals', 1e64, ...
    'MaxIter', 1e64, 'UseParallel', true);

% Build initial x value as x0
% ----- Your code here (7) ----- (slide 29)

% 5 for intrinsic
% 3 for translation, 3 for rotation, total 6 for each checkerboard image
x0 = zeros(5 + 6 * size(imagePoints, 3), 1);  % modify this line
x0(1:5, 1) = [alpha gamma beta u0 v0];
for nv = 1:numView
    start_idx = 5 + (nv - 1) * 6 + 1;
    x0(start_idx : start_idx + 2, 1) = rotationMatrixToVector(Rt(:, 1:3, nv)');
    x0(start_idx + 3 : start_idx + 5, 1) = Rt(:, 4, nv);
end

% Non-least square optimization
% Read [https://mathworks.com/help/optim/ug/lsqnonlin.html] for more information
[objective] = @(x) func_calibration(imagePoints, worldPoints, x);  % Done
[x_hat, ~, ~, ~, ~] = lsqnonlin(objective,x0,[],[],options);


%% Build camera parameters
rvecs = zeros(numView, 3);
tvecs = zeros(numView, 3);

% Extract intrinsic matrix K, rotation vectors and translation vectors from x_hat
% ----- Your code here (8) -----: Done
K = [x_hat(1) x_hat(2) x_hat(4);
            0 x_hat(3) x_hat(5);
            0        0        1;];

for nv = 1:numView
    start_idx = 5 + (nv - 1) * 6 + 1;
    rvecs(nv, :) = x_hat(start_idx : start_idx + 2, 1);
    tvecs(nv, :) = x_hat(start_idx + 3 : start_idx + 5, 1);
end

% Generate cameraParameters structure
cameraParams = cameraParameters('IntrinsicMatrix', K', ...
    'RotationVectors', rvecs, 'TranslationVectors', tvecs, ...
    'WorldPoints', worldPoints, 'WorldUnits', 'mm', ...
    'imageSize', imageSize) ; 

% Uncomment this line after you implement this function to calculate
% reprojection errors of your camera parameters.
reprojected_errors = imagePoints - cameraParams.ReprojectedPoints;

cameraParams = cameraParameters('IntrinsicMatrix', K', ...
    'RotationVectors', rvecs, 'TranslationVectors', tvecs, ...
    'WorldPoints', worldPoints, 'WorldUnits', 'mm', ...
    'imageSize', imageSize, 'ReprojectionErrors', reprojected_errors) ; 
