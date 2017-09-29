%This code will stablish communication with the ArgosP100 and capture the 
%depth data

%Opening port
hndl =  pmdOpen('C:\Users\x\Desktop\20130722_Support_CD_v1.2\PMDMDK\win64\digicam',' ','C:\Users\x\Desktop\20130722_Support_CD_v1.2\PMDMDK\win64\digicamproc',' ');

%Turning on Bilateral Filtering
bifiltstatus = pmdProcessingCommand(hndl, 'SetBilateralFilter on');

%Setting the number of Desired samples (recomended default 50)
Rep=50;

%Initilizing matrices (to improve speed - 160 x 120 device capabilities)
disttotal=zeros(160,120);
disttotal2=zeros(160,120);
dist=zeros(160,120,Rep);
distb=zeros(160,120,Rep);

%Start depth captures of the Base alone
for imdepthcard = 1:1:Rep
    %fetch a frame from usb controller
    pmdUpdate(hndl);
    
    %get distance (depth) data in meters
    dist(:,:,imdepthcard) = pmdGetDistances(hndl);
    
    %total distance gather from the samples being stored
    disttotal = dist(:,:,imdepthcard) + disttotal;
    
    %displaying a number as each sample is captured
    fprintf('.%d',imdepthcard)
end

fprintf('\n Place the book now and hit enter \n')

pause

%Start depth captures of the Book + Base
for imdepthbook = 1:1:Rep
    %fetch a frame from usb controller
    pmdUpdate(hndl);
    
    %get distance (depth) data in meters
    distb(:,:,imdepthbook) = pmdGetDistances(hndl);
    
    %total distance gather from the samples being stored
    disttotal2 = distb(:,:,imdepthbook) + disttotal2;
    
    %displaying a number as each sample is captured
    fprintf('.%d',imdepthbook)
end

%end fo depth capture
fprintf('\n end \n')

%Capturing amplitude data of Base + Book
ampdata=pmdGetAmplitudes(hndl);
ampdata1=ampdata/(max(max(ampdata)));
figure;imshow(rot90(ampdata1,1));

%%Capturing plausability flags data of Base + Book
flags = pmdGetFlags(hndl);          
figure;imshow(rot90(mat2gray(flags),1));

%closing connection to camera
pmdClose(hndl);

%Calculating averages of Captured depth samples
distaveragecard = disttotal/Rep;
distaveragebook = disttotal2/Rep;

%Estimated Height from averages 
RawHeight=distaveragecard-distaveragebook;
figure;mesh(rot90(RawHeight,1));    %display mesh
view([0,-90])

save C:\Users\x\Desktop\TemTest\DepthCapP2T11.mat