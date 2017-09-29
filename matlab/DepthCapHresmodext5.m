PixperCm=2668/58;
dhi=DepthMcm(:,2:colDM)-DepthMcm(:,1:(colDM-1));
dx=1./PixperCm;
dlip=((dx.^2+dhi.^2).^(1/2)).*PixperCm;


dlip2(:,1)=ones(rowDM,1);       %first location is always 1
dlip2(:,2:colDM)=dlip;

FxPixel2=cumsum(dlip2,2);
FyPixel2=cumsum(ones(rowDM,colDM),1);

Xvec2=reshape(FxPixel2-Xcent,numel(FxPixel2),1);%readjusting vector along the center
Yvec2=reshape(Ycent-FyPixel2,numel(FyPixel2),1);%readjusting vector along the center

tic
GI2=zeros(rowDM,colDM,3);
Xi2t=(1:colDM)-Xcent;
Yi2t=Ycent-(1:rowDM);
[XI2t,YI2t]=meshgrid(Xi2t,Yi2t);

for n=1:3
    Gcolor2=double(RevGIu(:,:,n));
    Gvec2=reshape(Gcolor2,numel(Gcolor2),1);
    %GIf2=TriScatteredInterp(Xvec2,Yvec2,Gvec2,'natural');
    %GIf2=TriScatteredInterp(Xvec2,Yvec2,Gvec2,'linear');
    GIf2=TriScatteredInterp(Xvec2,Yvec2,Gvec2,'nearest');
    GI2(:,:,n)=GIf2(XI2t,YI2t);    
end

GIu2=uint8(GI2);
figure, imshow(GIu2);
toc
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results\T10r\CutFlatRGBImP2T10rInt2Dx3CubwNatext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results\T10r\CutFlatRGBImP2T10rInt2Dx3CubwLinext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results\T10r\CutFlatRGBImP2T10rInt2Dx3CubwNeaext.tiff', 'Resolution', 180);

%imwrite(GIu2,'C:\Users\x\Desktop\tilt\CutFlatRGBImP2T14Int2Dx3CubwLinext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\tilt\CutFlatRGBImP2T14rInt2Dx3CubwLinext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImJ2T14r1Int2Dx3CubwLinext.tiff', 'Resolution', 180);

%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwNatextJ2SHM010.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwLinextJ2SHM075.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwNeaextJ2SHM075.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwNeaextJ2SHM075u.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwNeaextJ2SHM074r.tiff', 'Resolution', 180);
imwrite(GIu2,'C:\Users\x\Desktop\ABBYY_OCR_results_J2\CutFlatRGBImInt2Dx3CubwNeaextJ2SHM074ru.tiff', 'Resolution', 180);

%imwrite(GIu2,'C:\Users\x\Desktop\TemTest\CutFlatRGBImDepthCapAlign4v3Int2Dx3LinwNeaext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\TemTest\CutFlatRGBImDepthCap4Int2Dx3CubwLinext.tiff', 'Resolution', 180);
%imwrite(GIu2,'C:\Users\x\Desktop\TemTest\CutFlatRGBImDepthCapAlign5Int2Dx3CubwNatext.tiff', 'Resolution', 180);

%imwrite(GIu2,'C:\Users\x\Desktop\TemTest\CutFlatRGBImHighcurb3Int2Dx3CubwLinext.tiff', 'Resolution', 180); %Highcurb1

%imwrite(GIu2,'C:\Users\x\Desktop\TemTest\CutFlatRGBImP2T10Int2Dx3CubwNeaext.tiff', 'Resolution', 180); %P2TX