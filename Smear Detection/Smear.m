pkg load image
pkg load optim

%loading the average image
Input1 = imread('Enter your image path here');

%converting the image to gray scale
grey = rgb2gray(Input1);
figure;
imshow(grey);

%adjusting the parameters of the image like brightness and contrast
J = imadjust(grey);
figure;
imshow(J);

%sharpening the image using the unsharp filter
H = fspecial('unsharp');
sharpened = imfilter(J,H);
figure;
imshow(sharpened);

% figuring out the edges using sobel
BW = edge(sharpened,'sobel');
figure;
imshow(BW);

%converting it to a binary image
BW2 = im2bw(sharpened);
figure;
imshow(BW2);

%Finding out the gradient of the image
[Gmag, Gdir] = imgradient(sharpened);

%Gradient magnitude
figure;
imshow(Gmag);

%Final image. Using median filtering. 
Kmedian = medfilt2(Gmag);
figure;
imshow(Kmedian);

figure;
imshow(Gdir);

