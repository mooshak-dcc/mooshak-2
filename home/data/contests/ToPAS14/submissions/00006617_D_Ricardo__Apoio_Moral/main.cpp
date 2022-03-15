#include <iostream>
#include <cstring>
#include <string>
#include <fstream>
#include <cstdlib>

using namespace std;

int main()
{
    int v[24];
    int data, stock;
    float resultado;
    int resultado1;
    for(int i=0; i<24; i++){

    cin>>data>>v[i];
    }
    resultado=( (v[23]+v[22]+v[21])/3+(v[12]))/2;
    resultado1=( (v[23]+v[22]+v[21])/3+(v[12]))/2;

    if((resultado - resultado1)<0.5){
    cout<<resultado1+1<<endl;
    }else{
    cout<<resultado<<endl;
    }
    return 0;
}
