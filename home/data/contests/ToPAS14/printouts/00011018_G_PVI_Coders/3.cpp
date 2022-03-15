#include <iostream>
#include <vector>
#include <string>

using namespace std;

char tabela[20][20];

int main() {
    int s, m, n;
    cin >> s >> m >> n;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j<n; j++) {
            char k;
            cin >> k;
            tabela[j][m-1-i] = k;
        }
    }
    int N;
    cin >> N;
    int posx, posy;
    int econ = s;
    bool d = false;
    bool fora = false;
    bool aniqui = false;
    posx = 1; posy=1;
    for (int i = 0; i <N ; i++) {
        if (aniqui == true) {
            break;
        }
        cout << posx << " " << posy << " " << tabela[posx-1][posy-1]<< endl;
        char k;
        int l;
        cin >> k >> l;
        while (l > 0) {
            if (posx > n or posy > m) {
                fora = true;
                econ += 10;
                if (k == 'N' and d == true) {
                    posy -= 1;
                }
                else if (k == 'N' and d == false) {
                    posy += 1;
                }
                else if (k == 'S' and d == true) {
                    posy += 1;
                }
                else if (k == 'S' and d == false) {
                    posy -= 1;
                }
                else if (k == 'E') {
                    posx += 1;
                }
                else if (k == 'W') {
                    posx -= 1;
                }
            }
            else {
                fora = false;
                if (tabela[posx-1][posy-1] == 'T') {
                    int taxa = econ/10;
                    econ -= taxa;
                }
                else if (tabela[posx-1][posy-1] == 'I') {
                    int taxa = (5*econ)/10;
                econ += taxa;
                }
                else if (tabela[posx-1][posy-1] == 'X') {
                    if (d == true) {
                        d = false;
                    }
                    else {
                        d = true;
                    }
                }
                else if (tabela[posx-1][posy-1] == 'F') {
                    cout << "Aniquilado" << endl;
                    aniqui = true;
                    break;
                }
                if (k == 'N' and d == true) {
                    if (posy == 1) {
                        d = false;
                    }
                    else {
                        posy -= 1;
                    }
                }
                else if (k == 'N' and d == false) {
                    posy += 1;
                }
                else if (k == 'S' and d == true) {
                    posy += 1;
                }
                else if (k == 'S' and d == false) {
                    if (posy == 1) {
                        d = false;
                    }
                    else {
                        posy -= 1;
                    }
                }
                else if (k == 'E') {
                    posx += 1;
                }
                else if (k == 'W') {
                    if (posx == 1) {
                        posx = 1;
                    }
                    else {
                        posx -= 1;
                    }
                }
            }
            l--;
        }
    }
    if (aniqui == false) {
        if (fora = false and d == false) {
            cout << "Dentro(" << posx << "," << posy<< "):" << econ << ":" << "ND" << endl;
        }
        else if (fora = false and d == true) {
            cout << "Dentro(" << posx << "," << posy<< "):" << econ << ":" << "D" << endl;
        }
        else if (fora = true and d == true) {
            cout << "Fora(" << posx << "," << posy<< "):" << econ << ":" << "D" << endl;
        }
        else if (fora = true and d == false) {
            cout << "Fora(" << posx << "," << posy<< "):" << econ << ":" << "ND" << endl;
        }
    }
    return 0;
}
