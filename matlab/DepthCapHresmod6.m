

Nz=58.05; %Calculated NZ value (cm)

DepthM=Intcutim2; %Depth Map (meters)
%figure,mesh(DepthM);

[rowDM,colDM]=size(DepthM); %size of Depth Map
DepthMcm=DepthM.*100; %Depth Map (cm)

Xcent=round(colDM/2);  %to conver to x-cart plane max number of cols divide 2
                       %Left half would be negative, Right half positive

Ycent=round(rowDM/2);  %to conver to y-cart plane max number of rows divide 2
                        %Top half would be positive, Bottom negative  
%Window selection
Xoff=[-1/2,0,1/2];      %X offsets for increase sampling  (3x3)
Yoff=[-1/2,0,1/2];      %Y offsets for increase sampling  (3x3)

%Xoff=[-2/5,-1/5,0,1/5,2/5];      %X offsets for increase sampling (5x5)
%Yoff=[-2/5,-1/5,0,1/5,2/5];      %Y offsets for increase sampling (5x5)

Jend=size(Xoff,2);
Kend=size(Yoff,2);

RevGI=zeros(rowDM,colDM,3); %Initializing for speed
RevGIA=zeros(rowDM,colDM,3); %Initializing for speed

[XIt,YIt]=meshgrid((1:colDM)-Xcent,Ycent-(1:rowDM)); % creating mesh grid with center (0,0)

tic
                                              

for j=1:Jend;
    for k=1:Kend;

        Xnt=XIt+Xoff(j);
        Ynt=YIt+Yoff(k);

        Xtrap=mean([DepthMcm(1,:),DepthMcm(:,1)',DepthMcm(rowDM,:),DepthMcm(:,colDM)']);
        nDepthMcm=interp2(XIt,YIt,DepthMcm,Xnt,Ynt,'cubic',Xtrap); %sample new DepthHeight

        TransMod=(Nz-nDepthMcm)./Nz; %Transformation value
        RevTransMod=TransMod.^-1; %Return Transformation value

        RevFxCart=RevTransMod.*XIt;    %Reverse X-location 
        RevFyCart=RevTransMod.*YIt;    %Reverse Y-location

        RevXvec=reshape(RevFxCart,numel(RevFxCart),1);%converting RevFx matrix to vector
        RevYvec=reshape(RevFyCart,numel(RevFyCart),1);%converting RevFy matrix to vector

        %Gray scale recalculation
            for m=1:3
                RevGcolor=double(CutRGBIm(:,:,m));
                RevGIF=interp2(XIt,YIt,RevGcolor,RevXvec,RevYvec,'cubic');
                RevGI(:,:,m)=reshape(RevGIF,rowDM,colDM);
            end
            
        %Average accumulation
        RevGIA=(RevGI./(Jend*Kend))+ RevGIA;
                
    end
end

toc

RevGIu=uint8(RevGIA);

figure, imshow(RevGIu);
figure, imshow(CutRGBIm);

%imwrite(CutRGBIm,'C:\Users\x\Desktop\tilt\CutRGBImP2T14.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\tilt\CutFlatRGBImP2T14Int2Dx3Cub.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\tilt\CutFlatRGBImP2T14rInt2Dx3Cub.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2T14r1Int2Dx3Cub.tiff', 'Resolution', 180);

%%%imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2Int2Dx3CubSHM075.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2Int2Dx3CubSHM075u.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2Int2Dx3CubSHM074r.tiff', 'Resolution', 180);
imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2Int2Dx3CubSHM074ru.tiff', 'Resolution', 180);


%imwrite(RevGIu,'C:\Users\x\Desktop\ABBYY_OCR_results\T10r\CutFlatRGBImP2T10rInt2Dx3Cub.tiff', 'Resolution', 180);

%imwrite(CutRGBIm,'C:\Users\x\Desktop\TemTest\CutRGBImDepthCap4.tiff', 'Resolution', 180);
% %imwrite(RevGIu,'C:\Users\x\Desktop\TemTest\CutFlatRGBImDepthCapAlign4v3D2n2.tiff', 'Resolution', 180);
%imwrite(RevGIu,'C:\Users\x\Desktop\TemTest\CutFlatRGBImDepthCap4Int2Dx3Cub.tiff', 'Resolution', 180);

%imwrite(CutRGBIm,'C:\Users\x\Desktop\TemTest\CutRGBImHighcurb3.tiff', 'Resolution', 180); %Highcurb1
%imwrite(RevGIu,'C:\Users\x\Desktop\TemTest\CutFlatRGBImHighcurb3Int2Dx3Cub.tiff', 'Resolution', 180); %Highcurb1

%imwrite(CutRGBIm,'C:\Users\x\Desktop\TemTest\CutRGBImP2T4c.tiff', 'Resolution', 180); %P2TX
%imwrite(RevGIu,'C:\Users\x\Desktop\TemTest\CutFlatRGBImP2T4cInt2Dx3Cub.tiff', 'Resolution', 180); %P2TX

