function [depthMap, disparityMap] = estimateDepth(leftImage, rightImage, stereoParameters, maxDisparity, windowSize, aggrFilterSize)
% This function estimate disparity and depth values from left and right
% images. You should calculate disparty map first and then convert the
% disparity map to depth map using left camera parameters.
 
% Function inputs:
% - 'leftImage': rectified left image.
% - 'rightImage': rectified right image.
% - 'stereoParameters': stereo camera parameters.
 
% Function outputs:
% - 'depth': depth map of left camera.
% - 'disparity': disparity map of left camera.
 
leftImageGray = rgb2gray(im2double(leftImage));
rightImageGray = rgb2gray(im2double(rightImage));  % 582 1192
 
translation = stereoParameters.TranslationOfCamera2;
baseline = norm(translation);
focalLength = stereoParameters.CameraParameters1.FocalLength(1);
 
disparityMap = zeros(size(leftImageGray));
depthMap = zeros(size(leftImageGray));
% ----- Your code here (10) -----
 
% minDisparity = 0;
if nargin <= 3
    maxDisparity = 130;
    windowSize = 7;
    aggrFilterSize = 33;
end
 
% costVolume
costVolume = getCostVolumeNCC(leftImageGray, rightImageGray, windowSize, maxDisparity, aggrFilterSize);
 
% Disparity Map
[h, w] = size(leftImageGray);  % 582 1192
for i = 1:h
    for j = 1:w
        [~, minCostDisparity] = min(costVolume(i, j, :));
        disparityMap(i, j) = minCostDisparity;
    end
end
 
% Depth Map
depthMap(:, :) = (focalLength * baseline) ./ disparityMap(:, :);