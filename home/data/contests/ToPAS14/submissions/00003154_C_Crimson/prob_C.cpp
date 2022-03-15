#include <iostream>
#include <math.h>

using namespace std;

int main(){

    int lx, ly, m=0, mx, my, i, rx, ry, n;
    string t, tr;

    cin >> rx;
    cin >> ry;
    cin.ignore();
    cin >> tr;
    cin >> n;
    if (n > 0)
    {
        for (i=0;i<n;i++)
        {
            cin >> lx;
            cin >> ly;
            cin.ignore();
            cin >> t;
            if (((pow((lx-rx), 2) + pow((ly-ry), 2)) < m && t==tr)|| i ==0)
            {
                m = pow((lx-rx), 2) + pow((ly-ry), 2);
                mx = lx;
                my = ly;
            }
        }
        cout << mx << " " << my << " " << tr << " " << m << endl;
    }
    else cout<<"Nenhum"<<endl;

    return 0;
}
