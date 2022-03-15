#include <iostream>

using namespace std;

int main(){
    int a[24], c[24], media=0, b, mediames=0;
    for(b=0; b<24; b++){
    cin>>a[b]>>c[b];
    if(b>0){
    if(a[b]<=a[b-1]){
    cin>>a[b]>>c[b];
    }
    }
    if(c[b]>1000){
    cin>>a[b]>>c[b];
    }
    }
    media=(c[23]+c[22]+c[21])/3;
    mediames=(c[12]+c[0])/2;
    media=media+mediames;
    cout<<media<<endl;
}
