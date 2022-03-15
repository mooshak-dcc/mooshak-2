// Rahmen fuer ACM Contest
// Mai 2001 

// @JUDGE_ID: 10428AE xxx C++

using namespace std;

#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <strings.h>
#include <stdio.h>
#include <malloc.h>
#include <math.h>

//#include <string>
//#include <vector>
//#include <map>

#define DEBUGN(x) cerr << x
#define DEBUG(x) cerr << x << endl
//#define DEBUGN(x)
//#define DEBUG(x)

//#define INPUT_FILE "testme.in"
//#define OUTPUT_FILE "testme.out"

#define TRUE (0 == 0)
#define FALSE !TRUE

#define MAX(a,b) ((a>b) ? a : b)
#define MIN(a,b) ((a<b) ? a : b)

#ifdef INPUT_FILE 
ifstream foo(INPUT_FILE);
istream& in = foo;
#else
istream& in = cin;
#endif

#ifdef OUTPUT_FILE
ofstream bar(OUTPUT_FILE);  
ostream& out = bar;
#else
ostream& out = cout;
#endif

// Insert some templates here

int main( int argc, char** argv) { 
    int N;
    char o[10][10];
    char t[10][10];

    for (;;) {

	in >> N;
	if (N == 0) break;

	char to[N][N];   // array for storing transformations
	char to2[N][N];

	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < N; j++) {
		in >> o[i][j];
	    }

	    for (int j = 0; j < N; j++) {
		in >> t[i][j];
	    }
	}


	// test for 'preserved'
	int preserved = TRUE;

	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < N; j++) {
		if (o[i][j] != t[i][j]) {
		    preserved = FALSE;
		    break;
		}
	    }
	    if (!preserved) break;
	}

	if (preserved) {
	    out << "Preserved" << endl;
	    continue;
	}

	// test for a rotation
	int angle = -1;
	int counter;

	for (int i = 0; i < N; i++)
	    for (int j = 0; j < N; j++) to2[i][j] = o[i][j];

	for (int a = 1; a <= 3; a++) {
	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++) {
		    to[j][i] = to2[N-i-1][j];
		}

	    counter = 0;
	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
		    if (t[i][j] == to[i][j]) counter++;

	    if (counter == (N*N)) {
		angle = a;
		break;
	    }

	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++) to2[i][j] = to[i][j];
	}

	if (angle != -1) {
	    out << "Rotated through " << (angle * 90) << " degrees" << endl;
	    continue;
	}

	// test for reflection
	int reflection = TRUE;

	for (int i = 0; i < N; i++) {
	    for (int j = 0; j < N; j++) {
		if (o[i][j] != t[N-i-1][j]) {
		    reflection = FALSE;
		    break;
		}
	    }
	    if (!reflection) break;
	}

	if (reflection) {
	    out << "Reflected" << endl;
	    continue;
	}

	// test for combination	
	char ro[N][N];

	for (int i = 0; i < N; i++)
	    for (int j = 0; j < N; j++) ro[i][j] = o[N-i-1][j];// get reflected

	angle = -1;
	for (int i = 0; i < N; i++)
	    for (int j = 0; j < N; j++) to2[i][j] = ro[i][j];

	for (int a = 1; a <= 3; a++) {
	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++) {
		    to[j][i] = to2[N-i-1][j];
		}

	    counter = 0;
	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
		    if (to[i][j] == t[i][j]) counter++;

	    if (counter == (N*N)) {
		angle = a;
		break;
	    }

	    for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++) to2[i][j] = to[i][j];
	}

	if (angle != -1) {
	    out << "Reflected and rotated through " << (angle * 90) << " degrees" << endl;
	    continue;
	}

	out << "Improper" << endl;
    }
}
