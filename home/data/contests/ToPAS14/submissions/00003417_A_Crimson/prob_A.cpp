#include <iostream>

using namespace std;

int main(){

    int a,m,d;
    int a1,a2,a3,a4,a5;

    cin >> a >> m >> d;
    if (m<3)
    {
        a -= 1;
        m += 12;
    }
    a1 = a/100;
    a2 = a1/4;
    if (a>1582 || (a==1582 && m>10 )|| (a==1582 && m==10 && d>15))
    {
        a3 = 2 - a1 + a2;
    }
    //if ((a=1582 && m<=10 && d<=4) || (a<1582))
    else
    {
        a3=0;
    }
    a4 = (int)(365.25*(a+4716));
    a5 = (int)(30.6001*(m+1));
    cout << (a4 + a5 + d + a3 - 1524) << endl;
    return 0;
}
