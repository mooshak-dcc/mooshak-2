#include <iostream>
#include <vector>
#include <string>

using namespace std;

int tabela[200][200];

int main() {
    int a,b,c,d,e;
    cin >> a >> b >> c >> d >> e;
    int n;
    d = d*60;
    e = e*60;
    cin >> n;
    for (int i=0; i<n; i++) {
        for (int j = 0; j<n; j++) {
            int k;
            cin >> k;
            tabela[i][j] = k;
        }
    }
    int k, h;
    k = 1; h = 1;
    int minprob = 10000;
    int nminprob = 0;
    while (k!= 0 and h != 0) {
        cin >> k >> h;
        bool cont = true;
        h = h*60;
        //cout << k << " " << h << endl;
        int nprob = 0;
        if (h<d or h > e) {
            cont = false;
        }

            bool origem = false;
            bool destino = false;
            int tmptotal = 0;
            vector<int> trocos;
            for(int i = 0; i <k; i++) {
                int vi;
                if (i!=k-1) {
                int ni, di;
                cin >> vi >> ni >> di;
                trocos.push_back(vi);
                if (vi == b) {
                    destino = true;
                }
                if (vi == a and destino == true) {
                    cont = false;
                }
                else if (vi == a and destino == false) {
                    origem == true;
                }
                if (ni < c and origem == true and destino == false) {
                    cont = false;
                }
                if (origem == true and destino == false) {
                    tmptotal += di;
                }
                if (tmptotal > e-d) {
                    cont = false;
                }
                if (i >0 and tabela[trocos[i]-1][trocos[i-1]-1] == 1 and origem == true and destino == false) {
                    nprob++;
                }
                }
                else {
                cin >> vi;
                trocos.push_back(vi);
                if (vi == b) {
                    destino = true;
                }
                if (vi == a and destino == true) {
                    cont = false;
                }
                else if (vi == a and destino == false) {
                    origem == true;
                }
                if (i >0 and tabela[trocos[i]-1][trocos[i-1]-1] == 1 and origem == true and destino == false) {
                    nprob++;
                }
                }
                cout << i << " "<<vi << " " << cont<< endl;
            }
            if (nprob < minprob and cont == true and destino == true and origem == true) {
                minprob = nprob;
                nminprob = 1;
            }
            else if (nprob == minprob and cont == true and destino == true and origem == true) {
                nminprob++;
            }

    }
    if (minprob == 10000) {
        cout << "Impossivel" << endl;
    }
    else {
        cout << nminprob << " "<< minprob << endl;
    }

    return 0;
}
