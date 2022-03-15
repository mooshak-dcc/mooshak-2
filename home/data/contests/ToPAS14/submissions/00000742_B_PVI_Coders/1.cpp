#include <iostream>
#include <vector>

using namespace std;

int main() {
    int N;
    vector<int> numeros;
    cin >> N;
    for (int i = 0; i< N; i++) {
        int k;
        cin >> k;
        numeros.push_back(k);
    }
    int ctd = 0;
    for (vector<int>::iterator it = numeros.begin()+1; it != numeros.end()-1; it++) {
        if (2*(*(it-1)) < *it and *it > 2*(*(it+1)) ) {
            ctd++;
        }
    }
    cout << ctd << endl;
    return 0;
}
