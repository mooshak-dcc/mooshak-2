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
    int x;
    for(int i=0; i<24; i++){

    cin>>data>>v[i];
    }
    resultado=( (v[23]+v[22]+v[21])/3+(v[12]+v[0])/2)/2;
    resultado1=( (v[23]+v[22]+v[21])/3+(v[12]+v[0])/2)/2;
    x=resultado -resultado1;
    resultado1= resultado - x;
    cout<<resultado1<<endl;

}
