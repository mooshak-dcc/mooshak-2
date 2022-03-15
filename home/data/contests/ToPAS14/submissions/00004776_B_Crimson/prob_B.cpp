#include <iostream>

using namespace std;

int main(){

    int n,k(0),i,j,x;
    cin>>n;
    int v[1001];

    for(i=0; i<n; i++){
        cin>>v[i];
    }

    for(i=1; i<n-1; i++){
        if ((v[i] > (v[i+1])*2 ) && ( v[i] > (v[i-1]*2) ))
            k++;
    }
    cout << k << endl;
    return 0;
}
