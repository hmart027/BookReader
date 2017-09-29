close all
clear
clc
%baseimg=imread('Depthalign5.jpg'); %HD Image
%baseimg=imread('Depthalign6.jpg'); %HD Image
%baseimg=imread('C:\Users\x\Desktop\2015Research\2.Publication 2\Images\Example1\DepthCap2HIm.jpg');
%baseimg=imread('C:\Users\x\Desktop\2015Research\2.Publication 2\Images\Example2\DepthCap4HIm.jpg');
%baseimg=imread('C:\Users\x\Desktop\Highcurb\HighCurb3.jpg');  %Highcurb1
%baseimg=imread('C:\Users\x\Desktop\P2\test4.jpg');  %P2TX
%baseimg=imread('C:\Users\x\Desktop\tilt\test14.jpg');  %P2TX
%baseimg=imread('C:\Users\x\Desktop\ABBYY_OCR_results_J2\J2RawData\test14J2r1.jpg');  %P2TX
%baseimg=imread('C:\Users\x\Desktop\ABBYY_OCR_results_J2\SHM\testSHM075.jpg');  %P2TX
baseimg=imread('C:\Users\x\Desktop\ABBYY_OCR_results_J2\SHMRot\testSHM074r.jpg');

%load('C:\Users\x\Desktop\TemTest\DepthCapAlign5.mat'); %Low res Depth
%load('C:\Users\x\Desktop\TemTest\DepthCapAlign6.mat'); %Low res Depth
%load('C:\Users\x\Desktop\2015Research\2.Publication 2\Images\Example1\DepthCap2.mat');
%load('C:\Users\x\Desktop\2015Research\2.Publication 2\Images\Example2\DepthCap4.mat');
%load('C:\Users\x\Desktop\Highcurb\DepthCapHighCurb3.mat'); %Highcurb1
%load('C:\Users\x\Desktop\P2\DepthCapP2T4.mat'); %P2TX
%load('C:\Users\x\Desktop\tilt\DepthCapP2T14.mat'); %P2TX
%load('C:\Users\x\Desktop\ABBYY_OCR_results_J2\SHM\DepthCapJ2SH075.mat'); %J2
load('C:\Users\x\Desktop\ABBYY_OCR_results_J2\SHMRot\DepthCapJ2SH074r.mat'); %J2

%inputimg=imread('AmpBase.tiff'); %Low res Image
inputimg=rot90(ampdata1,1); %Low res Image
DepthHeight=rot90(RawHeight,1); %Low res Depth

p=size(baseimg);
cpselect(inputimg,baseimg)  %recalculate new points in common

pause

tform=maketform('affine',input_points, base_points);
[inputimg_t] = imtransform(inputimg,tform,'XData',[1,p(2)],'YData',[1,p(1)],'Size',p(1:2));
figure;imshow(inputimg_t)
figure;imshow(baseimg)


%figure;mesh(DepthHeight);
%view([0,-90])
[DepthHeight_t] = imtransform(DepthHeight,tform,'XData',[1,p(2)],'YData',[1,p(1)],'Size',p(1:2));
%figure;mesh(DepthHeight_t);
%view([0,-90])


%figure;warp(DepthHeight_t,baseimg);

%CutRGBIm=baseimg(640:2000,497:2690,1:3); %Align4
%Intcutim2=DepthHeight_t(640:2000,497:2690); %Align4

%CutRGBIm=baseimg(640:1900,497:2690,1:3); %DepthCapAlign5
%Intcutim2=DepthHeight_t(640:1900,497:2690); %DepthCapAlign5

%CutRGBIm=baseimg(650:1910,440:2600,1:3); %DepthCapAlign6
%Intcutim2=DepthHeight_t(650:1910,440:2600); %DepthCapAlign6

%CutRGBIm=baseimg(490:1850,460:2620,1:3); %DepthCap2
%Intcutim2=DepthHeight_t(490:1850,460:2620); %DepthCap2

%CutRGBIm=baseimg(535:2135,645:2760,1:3); %DepthCap4
%Intcutim2=DepthHeight_t(535:2135,645:2760); %DepthCap4

%CutRGBIm=baseimg(535:2015,625:2595,1:3);  %Highcurb1
%Intcutim2=DepthHeight_t(535:2015,625:2595); %Highcurb1

%CutRGBIm=baseimg(590:2050,515:2505,1:3); %Highcurb2
%Intcutim2=DepthHeight_t(590:2050,515:2505); %Highcurb2

%CutRGBIm=baseimg(640:2110,616:2696,1:3); %P2T0
%Intcutim2=DepthHeight_t(640:2110,616:2696); %P2T0

%CutRGBIm=baseimg(670:2140,606:2636,1:3); %P2T1
%Intcutim2=DepthHeight_t(670:2140,606:2636); %P2T1

%CutRGBIm=baseimg(670:2120,606:2636,1:3); %P2T2 & T3 & T4 & T5
%Intcutim2=DepthHeight_t(670:2120,606:2636); %P2T2 & T3 & T4 &T5

%CutRGBIm=baseimg(650:2150,606:2636,1:3); %P2T2b
%Intcutim2=DepthHeight_t(650:2150,606:2636); %P2T2b

%CutRGBIm=baseimg(685:2045,530:2620,1:3); %P2T4b
%Intcutim2=DepthHeight_t(685:2045,530:2620); %P2T4b

%CutRGBIm=baseimg(745:1965,530:2620,1:3); %P2T4c
%Intcutim2=DepthHeight_t(745:1965,530:2620); %P2T4c

%CutRGBIm=baseimg(680:2090,590:2600,1:3); %P2T6
%Intcutim2=DepthHeight_t(680:2090,590:2600); %P2T6

%CutRGBIm=baseimg(650:2040,605:2645,1:3); %P2T7, T8, T9
%Intcutim2=DepthHeight_t(650:2040,605:2645); %P2T7, T8, T9

%CutRGBIm=baseimg(705:2035,660:2490,1:3); %P2T10
%Intcutim2=DepthHeight_t(705:2035,660:2490); %P2T10

%CutRGBIm=baseimg(450:1950,400:2690,1:3); %P2T4b
%Intcutim2=DepthHeight_t(450:1950,400:2690); %P2T4b

%CutRGBIm=baseimg(400:2000,420:2620,1:3); %J2T10r1
%Intcutim2=DepthHeight_t(400:2000,420:2620); %J2T10r1

%CutRGBIm=baseimg(385:2000,480:2630,1:3); %J2T10r2
%Intcutim2=DepthHeight_t(385:2000,480:2630); %J2T10r2

%CutRGBIm=baseimg(390:2000,490:2700,1:3); %J2T11r1
%Intcutim2=DepthHeight_t(390:2000,490:2700); %J2T11r1

%CutRGBIm=baseimg(400:1970,400:2600,1:3); %J2T11r2
%Intcutim2=DepthHeight_t(400:1970,400:2600); %J2T11r2

%CutRGBIm=baseimg(405:1980,300:2640,1:3); %J2T12r1
%Intcutim2=DepthHeight_t(405:1980,300:2640); %J2T12r1

%CutRGBIm=baseimg(400:1980,310:2680,1:3); %J2T12r2
%Intcutim2=DepthHeight_t(400:1980,310:2680); %J2T12r2

%CutRGBIm=baseimg(405:1975,350:2730,1:3); %J2T13r1
%Intcutim2=DepthHeight_t(405:1975,350:2730); %J2T13r1

%CutRGBIm=baseimg(405:1990,280:2680,1:3); %J2T13r2
%Intcutim2=DepthHeight_t(405:1990,280:2680); %J2T13r2

%CutRGBIm=baseimg(405:1990,280:2680,1:3); %J2T13r2
%Intcutim2=DepthHeight_t(405:1990,280:2680); %J2T13r2

%CutRGBIm=baseimg(510:1900,630:2550,1:3); %J2T13r2
%Intcutim2=DepthHeight_t(510:1900,630:2550); %J2T13r2

%CutRGBIm=baseimg(420:1970,480:2560,1:3); %J2f1t37r
%Intcutim2=DepthHeight_t(420:1970,480:2560); %J2f1t37r

CutRGBIm=baseimg(420:2000,505:2610,1:3); %J2f1t37r
Intcutim2=DepthHeight_t(420:2000,505:2610); %J2f1t37r

% %% Display mesh and image combined
% figure;warp(Intcutim2,CutRGBIm); %Removing 1 extra value to match
% view([-10,65])
% axis([0 2300 0 1600 0 0.08])  %
% xlabel('x-axis')
% ylabel('y-axis')
% zlabel('h(x,y) in m.')

%print('-f6','-djpeg','C:\Users\x\Desktop\TemTest\Cut3DHresDepthCapTrueFlatDisp4.jpg','-r300','-zbuffer'); %use zbuffer for bigger image
%figure;imshow(CutRGBIm)
imwrite(CutRGBIm,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutRGBImJ2SHM074r.tiff', 'Resolution', 180);
%save C:\Users\x\Desktop\TemTest\NewRegDepthCap4.mat
%save C:\Users\x\Desktop\TemTest\NewRegHighCurb3.mat %Highcurb1
%save C:\Users\x\Desktop\TemTest\NewRegDepthCapP2T14.mat %P2Tx
save C:\Users\x\Desktop\ABBYY_OCR_results_J2\NewRegDepthCapSHM074r.mat