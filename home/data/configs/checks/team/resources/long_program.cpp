//----------------------------------------------------------------------------
// básico
//----------------------------------------------------------------------------

#include <string>
#include <iostream>
#include <ostream>
#include <queue>
#include <set>
#include <vector>
#include <stdexcept>

#define TMPL(A) template<class A>
#define TMPL2(A,B) template<class A, class B>
#define TMPL3(A,B,C) template<class A, class B, class C>
#define TMPL4(A,B,C,D) template<class A, class B, class C, class D>
#define TMPL5(A,B,C,D,E) template<class A, class B, class C, class D, class E>
#define TMPL6(A,B,C,D,E,F) \
  template<class A, class B, class C, class D, class E, class F>

using namespace std;


//----------------------------------------------------------------------------
// iteração
//----------------------------------------------------------------------------

#define FOR(I,SIZE) for(size_t I = 0; I < SIZE; ++I)

#define FOR2(I,J,LINES,COLS) FOR(I, LINES) FOR(J, COLS)

#define IT_FOR_EACH(IT,I,BEGIN,END) for(IT I = BEGIN; I != END; ++I)

#define CT_FOR_EACH(CT,C,I) for(CT::iterator I = C.begin(); I != C.end(); ++I)

#define CT_FOR_EACH_C(CT,C,I) \
  for(CT::const_iterator I = C.begin(); I != C.end(); ++I)

TMPL(C) ostream& CT_PRINT(ostream& os, C const& c)
{
  CT_FOR_EACH_C(typename C, c, i)
    os << *i << " ";
  os << "\n";
}

TMPL(C) ostream& CT_PRINT2(ostream& os, C const& c)
{
  os << "[";
  bool first_time = true;
  CT_FOR_EACH_C(typename C,c,i)
  {
    if (first_time)
      first_time = false;
    else
      os << " ";
    os << *i;
  }
  return os << "]";
}

TMPL(IT) ostream& IT_PRINT(ostream& os, IT const& begin, IT const& end)
{
  os << "[";
  bool first_time = true;
  IT_FOR_EACH(IT,i,begin,end)
  {
    if (first_time)
      first_time = false;
    else
      os << " ";
    os << *i;
  }
  return os << "]";
}

//----------------------------------------------------------------------------
// inifinity
//----------------------------------------------------------------------------

#define INF INT_MAX
#define NEG_INF INT_MIN

#define INF_LESSER(A,B) (!(A == B) && (A < B || A == NEG_INF || B == INF))
#define INF_GREATER(A,B) (!(A == B) && (A > B || B == NEG_INF || A == INF))

#define INF_ADD(A,B,C) \
  if ((A == INF && B == NEG_INF) || \
      (A == NEG_INF && B == INF)) \
    throw runtime_error("indeterminação"); \
  else if (A == INF || B == INF) \
    C = INF; \
  else if (A == NEG_INF || B == NEG_INF) \
    C = NEG_INF; \
  else \
    C = A + B;

#define INF_SUB(A,B,C) \
  if (A == INF && B == NEG_INF) \
    throw runtime_error("indeterminação"); \
  else if (A == NEG_INF || B == INF) \
    C = NEG_INF; \
  else if (A == INF || B == NEG_INF) \
    C = INF; \
  else \
    C = A - B;

#define INF_ADD_SELF(A,B) INF_ADD(A,B,A)
#define INF_SUB_SELF(A,B) INF_SUB(A,B,A)

//----------------------------------------------------------------------------
// matrizes
//----------------------------------------------------------------------------

TMPL(T = int)
struct Mat
{
  size_t n_lines, n_cols;
  T** data;

  typedef T element_type;

  void create()
  {
    data = new T*[n_lines];
    FOR(i, n_lines)
      data[i] = new T[n_cols];
  }

  void destroy()
  {
    FOR(i, n_lines)
      delete [] data[i];
    delete [] data;
  }

  void copy(Mat<T> const& other)
  {
    FOR2(i, j, n_lines, n_cols)
      data[i][j] = other.data[i][j];
  }

  Mat(size_t n_lines, size_t n_cols, T init_value = T())
  : n_lines(n_lines), n_cols(n_cols)
  {
    create();
    FOR2(i, j, n_lines, n_cols)
      data[i][j] = init_value;
  }

  
  Mat(size_t n_lines, size_t n_cols, istream& is)
  : n_lines(n_lines), n_cols(n_cols)
  {
    create();
    FOR2(i, j, n_lines, n_cols)
    {
      T temp;
      is >> temp;
      if (!is)
      {
        destroy();
        throw runtime_error("erro na leitura da matriz");
      }
      data[i][j] = temp;
    }
  }

  operator T**() const // **YUCK**
  { return const_cast<T**>(data); }

  Mat<T>& operator=(Mat<T> const& other)
  {
    destroy();
    n_lines = other.n_lines;
    n_cols = other.n_cols;
    create();
    copy(other);
    return *this;
  }

  Mat(Mat<T> const& other)
  : n_lines(other.n_lines), n_cols(other.n_cols)
  { 
    create();
    copy(other);
  }

  ~Mat()
  { destroy(); }

};

TMPL(T)
ostream& operator<<(ostream& os, Mat<T> const& m)
{
  FOR(i, m.n_lines)
  {
    FOR(j, m.n_cols)
      os << m[i][j] << " ";
    os << endl;
  }
  return os;
}

//----------------------------------------------------------------------------
// grafos
//----------------------------------------------------------------------------

#define NIL -1
enum Color { WHITE, GRAY, BLACK };

struct Graph: public vector<set<size_t> >
{
  typedef set<size_t> adjs_t;
  typedef size_t vertex_t;

  Graph(size_t n_vertices)
    : vector<set<size_t> >(n_vertices) {};

  adjs_t& adjs(size_t v) const // **YUCK**
  { return const_cast<adjs_t&>((*this)[v]); }

  void add_edge(size_t vs, size_t vd)
  { (*this)[vs].insert(vd); }

  size_t n_vertices() const
  { return size(); }

  size_t n_edges() const
  {
    size_t n = 0;
    FOR(v, size())
      n += adjs(v).size();
    return n;
  }
};

#define G_FOR_EACH_VERTEX(G,V) FOR(V, G.n_vertices())

#define G_FOR_EACH_ADJ_EDGE(G,U,I) \
  for(Graph::adjs_t::iterator I = G[U].begin(); I != G[U].end(); ++I)

#define G_FOR_EACH_ADJ_EDGE_C(G,U,I) \
  for(Graph::adjs_t::const_iterator I = G[U].begin(); I != G[U].end(); ++I)

#define G_FOR_EACH_EDGE(G,U,I) \
  G_FOR_EACH_VERTEX(G,U) \
    G_FOR_EACH_ADJ_EDGE(G,U,I)

#define G_FOR_EACH_EDGE_C(G,U,I) \
  G_FOR_EACH_VERTEX(G,U) \
    G_FOR_EACH_ADJ_EDGE_C(G,U,I)


ostream& operator << (ostream& os, Graph const& g)
{
  os << "  Vertices: ";
  G_FOR_EACH_VERTEX(g, v)
    os << v << " ";
  os << "\n  Edges: ";
  G_FOR_EACH_EDGE_C(g, u, i)
    os << "(" << u << "," << *i << ") ";
  os << endl;
  return os;
}

//----------------------------------------------------------------------------
// BFS
//----------------------------------------------------------------------------

TMPL3(V,D,PI) void BFS(Graph& g, V s, D& d, PI& pi)
{
  vector<Color> color(g.size());

  G_FOR_EACH_VERTEX(g, u)
  {
    color[u] = WHITE;
    d[u] = INF;
    pi[u] = NIL;
  }

  color[s] = GRAY;
  d[s] = 0;
  pi[s] = NIL;

  queue<V> q;
  q.push(s);
  while (!q.empty())
  {
    V u = q.front();
    q.pop();

    G_FOR_EACH_ADJ_EDGE(g, u, i)
    {
      V v = *i;
      if (color[v] == WHITE)
      {
        color[v] = GRAY;
        d[v] = d[u] + 1;
        pi[v] = u;
        q.push(v);
      }
    }
    color[u] = BLACK;
  }
}

TMPL3(V,PI,C) bool GET_BFS_PATH(Graph& g, V s, V v, PI& pi, C& c)
{
  if (v == s)
    c.push_back(s);
  else if (pi[v] == NIL)
    return false;
  else
  {
    if (GET_BFS_PATH(g, s, pi[v], pi, c))
      c.push_back(v);
    else
      return false;
  }
  return true;
}

//----------------------------------------------------------------------------
// DFS
//----------------------------------------------------------------------------

struct default_f { TMPL(V) void operator()(V v) {} };

TMPL6(V,DF,PI,COLOR,ON_START,ON_FINISH)
void DFS_VISIT(Graph& g, V u, DF& d, DF& f, PI& pi, COLOR& color,
               int& time, ON_START on_start, ON_FINISH on_finish,
               bool test_for_DAG)
{
  color[u] = GRAY;
  d[u] = ++time;
  on_start(u);
  G_FOR_EACH_ADJ_EDGE(g, u, i)
  {
    V v = *i;
    if (color[v] == WHITE)
    {
      pi[v] = u;
      DFS_VISIT(g, v, d, f, pi, color, time, on_finish, test_for_DAG);
    }
    else if (test_for_DAG && color[u] == GRAY)
      throw runtime_error("o grafo não é um DAG");
  }
  color[u] = BLACK;
  f[u] = ++time;
  on_finish(u);
}

TMPL4(DF,PI,ON_START,ON_FINISH)
void DFS(Graph& g, DF& d, DF& f, PI& pi,
         ON_START on_start = default_f(), ON_FINISH on_finish = default_f(),
         bool test_for_DAG = false)
{
  vector<Color> color(g.size());

  G_FOR_EACH_VERTEX(g, u)
  {
    color[u] = WHITE;
    pi[u] = NIL;
  }
  int time = 0;
  G_FOR_EACH_VERTEX(g, u)
    if (color[u] == WHITE)
      DFS_VISIT(g, u, d, f, pi, color, time, on_start, on_finish, test_for_DAG);
}

//----------------------------------------------------------------------------
// Topological Sort
//----------------------------------------------------------------------------

TMPL(C)
struct ts_on_finish
{
  ts_on_finish(C& c)
    : c(c) {}

  TMPL(V) void operator()(V v)
  { c.push_front(v); }

  C& c;
};

TMPL(C) void TOPOLOGICAL_SORT(Graph& g, C& c)
{
  vector<int> d(g.size());
  vector<int> f(g.size());
  vector<int> pi(g.size());

  DFS(g, d, f, pi, ts_on_finish<C>(c));
};

//----------------------------------------------------------------------------
// Bellman Ford SSSP
//----------------------------------------------------------------------------

#define INITIALIZE_SINGLE_SOURCE(G,S,D,PI)    \
{                                             \
  for(size_t v = 0; v < g.n_vertices(); ++v)  \
  {                                           \
    D[v] = INF;                               \
    PI[v] = NIL;                              \
  }                                           \
  d[s] = 0;                                   \
}

#define RELAX(U,V,W,D,PI)     \
  if ((D[U] != INF) &&        \
      (D[V] == INF || D[V] > D[U] + W[U][V]))  \
  {                           \
    assert(D[U] != INF);      \
    assert(W[U][V] != INF);   \
    D[V] = D[U] + W[U][V];    \
    PI[V] = U;                \
  }

TMPL3(W, D, PI)
void BELLMAN_FORD_SSSP(Graph const& g, W const& w, size_t s, D& d, PI& pi)
{
  assert(d.size() == pi.size());
  assert(d.size() == g.n_vertices());

  INITIALIZE_SINGLE_SOURCE(g, s, d, pi)

  FOR(n, g.n_vertices()-1)
    G_FOR_EACH_EDGE_C(g, u, i)
    {
      size_t v = *i;
      RELAX(u, v, w, d, pi);
    }

  G_FOR_EACH_EDGE_C(g, u, i)
  {
    size_t v = *i;
    if (d[v] > d[u] + w[u][v])
      throw runtime_error("ciclo negativo");
  }
}

//----------------------------------------------------------------------------
// Dijkstra's SSSP
//----------------------------------------------------------------------------

TMPL(D)
struct greater_d
{
  greater_d(D& d): d(d) {}

  bool operator()(size_t v1, size_t v2) const
  {
    return (d[v2] != INF) &&
           (d[v1] == INF || d[v1] > d[v2]);
  }

  D& d;
};

TMPL3(W, D, PI)
void DIJKSTRA_SSSP(Graph const& g, W const& w, size_t s, D& d, PI& pi)
{
  assert(d.size() == pi.size());
  assert(d.size() == g.n_vertices());

  INITIALIZE_SINGLE_SOURCE(g, s, d, pi)

  typedef priority_queue<size_t, vector<size_t>, greater_d<D> > q_t;
  greater_d<D> dj_compare(d);
  q_t q(dj_compare);

  G_FOR_EACH_VERTEX(g, v)
    q.push(v);

  while(!q.empty())
  {
    size_t u = q.top();
    q.pop();
    G_FOR_EACH_ADJ_EDGE_C(g, u, i)
    {
      size_t v = *i;
      RELAX(u, v, w, d, pi)
    }
  }
}

//----------------------------------------------------------------------------
// Johnson's APSP
//----------------------------------------------------------------------------

TMPL3(W, D, PI)
void JOHNSON_APSP(Graph const& g, W const& w, D& d, PI& pi)
{
  assert(g.n_vertices() == pi.n_lines);
  assert(g.n_vertices() == d.n_lines);
  assert(pi.n_lines == pi.n_cols);
  assert(d.n_lines == d.n_cols);

  Graph g_linha(g.n_vertices() + 1);
  W w_linha(g.n_vertices() + 1, g.n_vertices() + 1, INF);

  G_FOR_EACH_EDGE_C(g, u, i)
  {
    size_t v = *i;
    g_linha.add_edge(u, v);
    w_linha[u][v] = w[u][v];
  }

  size_t s = g.n_vertices();
  G_FOR_EACH_VERTEX(g, v)
  {
    g_linha.add_edge(s, v);
    w_linha[s][v] = 0;
  }

  typedef vector<typename W::element_type> d_t;
  typedef vector<int> pi_t;
  d_t bf_d(g_linha.n_vertices(), INF);
  pi_t bf_pi(g_linha.n_vertices(), NIL);

  BELLMAN_FORD_SSSP(g_linha, w_linha, s, bf_d, bf_pi);

  d_t const& h = bf_d;

  G_FOR_EACH_EDGE(g_linha, u, i)
  {
    size_t v = *i;
    w_linha[u][v] = w_linha[u][v] + h[u] - h[v];
  }

  d_t dijkstra_d(g.n_vertices(), INF);
  pi_t dijkstra_pi(g.n_vertices(), NIL);

  G_FOR_EACH_VERTEX(g, u)
  {
    DIJKSTRA_SSSP(g, w_linha, u, dijkstra_d, dijkstra_pi);

    G_FOR_EACH_VERTEX(g, v)
    {
      d[u][v] = dijkstra_d[v] + h[v] - h[u];
      pi[u][v] = dijkstra_pi[v];
    }
  }
}


//----------------------------------------------------------------------------

#include <stdio.h>

int SIZE;

void show(Mat<char> const& m) {
  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      printf("%c", m[y][x]);
    }
    printf("\n");
  }
}

int test(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}



int putt(Mat<char>& m, int y) {
  if(!test(m)) return 0;
  if(y == SIZE) {
    return 1;
  }
  for(int x = 0; x < SIZE; x++) {
    Mat<char> temp = m;
    
    temp[y][x] = 'X';
    
    if(putt(temp, y+1)) {
      m = temp;
      return 1;
      }
  }
  return 0;
}

int put(Mat<char>& m, int y) {
  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {
	for(int yy = 0; yy < SIZE; yy++) {
	  for(int xx = 0; xx < SIZE; xx++) {
	    if(xx == x || yy == y || xx - x == yy - y || xx - x == yy + y || xx + x == yy - y || xx + x == yy + y) {
	      Mat<char> temp = m;
	      temp[y][x] = '0';
	      if(temp[yy][xx] != 'X') {
		temp[yy][xx] = 'X';
		if(test(temp)) {
		  m = temp;
		  return 1;
		}
	      }
	    }
	  }
	}
      }
    }
  }
  return 0;
}

int main()
{ 
  cin >> SIZE;
  Mat<char> matriz(SIZE, SIZE, cin);
  
  if(test(matriz)) {
    printf("YES\n");
  } else {
    printf("NO\n");
    
/*    FOR2(i, j, matriz.n_lines, matriz.n_cols)
      matriz.data[i][j] = '0';
  */  
    if(put(matriz, 0)) {
      printf("YES\n");
      show(matriz);
    } else {
      printf("NO\n");
    }
  }
}




int test1(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test2(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test3(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test4(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test5(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test6(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test7(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}

int test8(Mat<char> const& m) {

  for(int y = 0; y < SIZE; y++) {
    for(int x = 0; x < SIZE; x++) {
      if(m[y][x] == 'X') {

	for(int xx = 0; xx < x; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}
	for(int xx = x + 1; xx < SIZE; xx++) {
	  if(m[y][xx] == 'X') {
	    return 0;
	  }
	}

	for(int yy = 0; yy < y; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}
	for(int yy = y + 1; yy < SIZE; yy++) {
	  if(m[yy][x] == 'X') {
	    return 0;
	  }
	}


	for(int xx = x+1, yy = y+1; xx < SIZE && yy < SIZE; xx++, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x+1, yy = y-1; xx < SIZE && yy >= 0; xx++, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y+1; xx >= 0 && yy < SIZE; xx--, yy++) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}

	for(int xx = x-1, yy = y-1; xx >= 0 && yy >= 0; xx--, yy--) {
	  if(m[yy][xx] == 'X') {
	    return 0;
	  }
	}


      }
    }
  }
  return 1;
}
