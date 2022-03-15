#include <iostream>

using namespace std;

int main(){
    int i, ns, nf = 0;
    cin>>ns;
    int f[ns];
    for(i=0; i<ns; i++){
        cin>>f[i];

    }
    for(i=0; i<ns; i++){
        if(i!=0){
            if((f[i-1]*2)<f[i] and (f[i +1]*2)<f[i]){
                nf++;
            }

        }
    }
    cout<<nf<<endl;
}
