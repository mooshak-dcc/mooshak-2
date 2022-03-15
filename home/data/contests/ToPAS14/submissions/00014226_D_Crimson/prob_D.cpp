#include <iostream>
#include <math.h>

using namespace std;

int main(){

    int i,k=0,n;
    float m1,m2=0,mt;
    int v[24][3];

    for(i=0; i<24;i++){
        cin>>n;
        v[i][0]=n/100;
        v[i][1]=n%100;
        cin>>n;
        v[i][2]=n;
    }

    m1=trunc((v[23][2] + v[22][2] + v[21][2])/3);

    for(i=0; i<23;i++){
        if(v[i][1] == v[23][1]+1){
            m2=m2+v[i][2];
            k++;
        }
    }

    m2=trunc(m2/k);
    mt=trunc((m1+m2)/2);

    cout << mt << endl;

    return 0;
}
