#include <iostream>

using namespace std;

int main(){
    int a[24], c[24], media=0, b;
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
    media+=c[b];
    }
    media/=24;
    cout<<media<<endl;
}
