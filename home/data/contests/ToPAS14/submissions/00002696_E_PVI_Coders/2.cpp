#include <iostream>
#include <vector>
#include <string>

using namespace std;

int main() {
    int n;
    cin >> n;
    vector<string> palavras;
    for (int i = 0; i < n; i++) {
        string a;
        cin >> a;
        palavras.push_back(a);
    }
    int a=1;
    while (a != 0) {
        cin >> a;
        if (a == 0) {
            break;
        }
        else if (a == 1) {
            int c;
            cin >> c;
            for(int i = 0; i < c; i++) {
                string k;
                cin >> k;
                for(int j = 1; j <= n; j++) {
                    if (k == palavras[j-1] and i != c-1) {
                        cout << j << " ";
                    }
                    else if (k == palavras[j-1] and i == c-1) {
                        cout << j << endl;
                    }
                }
            }
        }
        else {
            int c;
            cin >> c;
            for(int i = 0; i < c; i++) {
                int k;
                cin >> k;
                if (i != c-1) {
                        cout << palavras[k-1] << " ";
                    }
                    else if ( i == c-1) {
                         cout << palavras[k-1] << endl;
                    }
            }
        }
    }
    return 0;
}
