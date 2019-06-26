function [costVolume] = getCostVolumeNCC(leftImage, rightImage, windowSize, maxDisparity, aggrFilterSize)
 
[h, w] = size(rightImage);
costVolume = zeros(h, w, maxDisparity);
windowBox = fspecial('average', windowSize);
aggrBox = fspecial("average", aggrFilterSize);
 
leftImage = padarray(leftImage, [0, maxDisparity], "replicate", "post");
 
leftMean = imfilter(leftImage, windowBox);
rightMean = imfilter(rightImage, windowBox);
 
leftStdev = sqrt(imfilter(leftImage.^2, windowBox) - leftMean.^2);
rightStdev = sqrt(imfilter(rightImage.^2, windowBox) - rightMean.^2);
 
leftImage = leftImage - leftMean;
rightImage = rightImage - rightMean;
  
for d = 1:maxDisparity
    ncc = (leftImage(:, d : d + w - 1) .* rightImage) ./ (leftStdev(:, d : d + w - 1) .* rightStdev);
    costVolume(:, :, d) = - ncc;
end
 
for d = 1:maxDisparity
    costVolume(:, :, d) = imfilter(costVolume(:, :, d), aggrBox);
end