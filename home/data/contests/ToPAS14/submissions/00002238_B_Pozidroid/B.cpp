#include <iostream>

using namespace std;

int main(){
int a, h=0;
cin>>a;
int b[a];
for(int c=0; c<a; c++){
cin>>b[c];
if(c>0){
if((b[c-1]*2)<b[c]){
h++;
}
}
}
cout<<h<<endl;
}
