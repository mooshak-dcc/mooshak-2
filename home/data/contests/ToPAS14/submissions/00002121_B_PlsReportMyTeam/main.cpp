#include <iostream>

using namespace std;

int main()
{
int o=0, N;
int caixa[1000];
cin >> N;

    for(int i=0; i<N;i++){
    cin>>caixa[i];
    }

    for(int i=1; i<N;i++){
    if (caixa[i]>caixa[i+1]*2){
    if(caixa[i]>caixa[i-1]*2){
    o++;
    }
    }
    }

    cout << o<<endl;
    return 0;
}
