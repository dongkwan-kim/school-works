function [objective] = func_calibration(imagePoints, worldPoints, x)
% Objective function to minimize eq.10 in Zhang's paper. 
% Size of input variable x is 5+6*n where n is number of checkerboard 
% images. An intrinsic matrix can be reconstructed from first five
% parameters, and the extrinsic matrix can be reconstructed from remain
% parameters.

% You should fill the variable hat_m which contains reprojected positions 
% of checkerboard points in screen coordinate.

% Function inputs:
% - 'imagePoints': positions of checkerboard points in a screen space.
% - 'worldPoints': positions of checkerboard points in a model space.
% - 'x': parameters to be optimized.

% Function outputs:
% - 'objective': difference of estimated values and real values.
    
numView = size(imagePoints,3);
hat_m = zeros(size(imagePoints));  % 54 2 6, hat_q?

% ----- Your code here (9) -----: Done

K = [x(1) x(2) x(4);
        0 x(3) x(5);
        0    0    1;];

Rt = zeros(3, 4, numView);
for nv = 1:numView
    start_idx = 5 + (nv - 1) * 6 + 1;
    Rt(:, 1:3, nv) = rotationVectorToMatrix(x(start_idx : start_idx + 2, 1))';
    Rt(:, 4, nv) = x(start_idx + 3 : start_idx + 5, 1);
    
    KR12t = K * [Rt(:, 1:2, nv) Rt(:, 4, nv)];  % 3 3
   
    hat_uvw = KR12t * [worldPoints ones(size(worldPoints, 1), 1)]';  % 3 54
    hat_m(:, :, nv) = (hat_uvw(1:2, :) ./ hat_uvw(3, :))';  % 54 2
end

objective = imagePoints - hat_m;