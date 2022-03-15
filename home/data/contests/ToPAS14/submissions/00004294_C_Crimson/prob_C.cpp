#include <iostream>
#include <math.h>

using namespace std;

int main(){

    int lx, ly, m=-1, mx, my, i, rx, ry, n, fl=0;
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
            if (((pow((lx-rx), 2) + pow((ly-ry), 2)) < m && t==tr)|| m ==-1)
            {
                m = pow((lx-rx), 2) + pow((ly-ry), 2);
                mx = lx;
                my = ly;
                if (i!=0)
                {
                    fl=1;
                }
            }
        }
        if (fl!=1)
        {
            cout<<"Nenhum"<<endl;
        }
        else cout << mx << " " << my << " " << tr << " " << m << endl;
    }
    else cout<<"Nenhum"<<endl;

    return 0;
}
