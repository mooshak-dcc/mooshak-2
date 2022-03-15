/*--------------------------------------------------------------- 
   Problem:   Buddy Memory Allocater
   Proponent: Fernando Silva, DCC-Univ. Porto / fds@ncc.up.pt
   Contest:   MIUP'2001
   Date:      16/09/2001
   Language:  C
   
   The problem is inspired in the Operating System books. 

   Input: U,L  Req[]={{A,70},{B,35},...}

   My solution uses:
     - a binary-tree (but nodes have a parent-pointer);
     - an array of lists indexed by block sizes (the elements 
       in the lists point to free blocks in the tree);
     - and an array indexed by procs-id that point to the tree 
       leaf-node representing the process. 

   I didn't spent much time trying to optimize this solution.

   Variables:

   bt       a binary-tree representing the buddy; 
            processes or free blocks are always at leaf-nodes;
   holes[]  holes[i] points to a list of free buddies of size 2^i
   pt[]     pt[i] points to tree-node representing a process with 
            identifier i+'A'

   Basic idea:
     
   The idea is that initially bt points to a free block size 2
   
   ^U;
   If the size, S, of the process being serviced is:
   (S>0) ALLOCATE_BLOCK:
      one has to a allocate a block for it. To do this:
      - determine the size of the block needed, say k.
      - get a free block of that size. This is implemented as a 
        recursive function that will split a buddy in two
        as necessary until a block of the targeted size is found.

        When a buddy of size i (2^i) is split in two buddies 
        of size (i-1) (2^(i-1)), one has to:
         - change the node corresponding to the actual buddy from
           HOLE to NODE;
         - create two child nodes corresponding to the new buddies, 
           each one is of type HOLE with size (i-1)
         - remove the ptr to the buddy-i from the list of free blocks (holes) 
           of that size, and
         - add to list of free blocks of size (i-1) pointers to the two
           buddies of size (i-1)

     - use a block from the free list of size k

   (s=0) UNALLOCATE_BLOCK
      one has to deallocate the block being used for the process:
      - modify corresponding node in the tree to type HOLE
      - add it to the corresponding free list
      - check through parent node whether there are two companion
        free blocks;
        - if it is the case, remove both nodes from the tree and 
          from the free list of that size,
        - modify the parent to become a HOLE
        - add the parent to the free list of its size

    To print the state of the buddy just do a depth-first tree traversal.
    Note that only leaf nodes (whose type is different from NODE) are output.

    INPUT DATA: in.1, in.2, ..., in.6

    OUTPUT DATA: out.1, out.2, ..., out.6

---------------------------------------------------------------*/
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MAX      20   /* 1 Megabyte */
#define MAXPROCS 100

#define HOLE  0
#define NODE  1

typedef struct btree BTree;
struct btree {
  char  type; /* capital letter -> proc; 0 - hole; 1 - node;*/
  int   psize;/* size of proc. if one is allocated          */
  int   bsize;/* size of block  is 2^bsize                  */
  BTree *parent;/* ptr to parent needed for merging buddies */
  BTree *left;  
  BTree *right;
};

typedef struct list List;
struct list {
  BTree *t;     /* ptr to a free-block in the btree */
  List  *next;
};

/* functions used in the program */
BTree *get_hole(int);
void unallocate_block(char);
void allocate_block(char,int);
void init_buddy();
void print_buddy(BTree *);
BTree *new_node(int,int,int,BTree *);
int find_blocksize(int); 
void  merge_buddies(BTree *, BTree *);

/* Global vars       */
List  *holes[MAX];   /* holes list indexed by block size     */
BTree *pt[MAXPROCS]; /* procs table                          */
int   u,l;           /* upper and lower block sizes          */
BTree *bt;           /* a binary-tree representing the buddy */

int main()
{
  char proc;
  int psize;

  scanf("%d %d\n",&u, &l);
  init_buddy();
  while (scanf("%c %d\n",&proc,&psize)!=EOF) {
    if (psize==0) 
      unallocate_block(proc);
    else
      allocate_block(proc,psize);
  }
  print_buddy(bt);
  exit(0);
}

void init_buddy() {
  List *l;

  /* the tree consists of one node of type Hole with size 2^U */
  /* hole[u] points to that hole node                         */
 
  bt= new_node(HOLE,0,u,NULL);
  l= (List *) malloc (sizeof(List));
  l->t= bt;
  holes[u]=l;
}

void allocate_block(char proc, int psize) {
  int i;
  BTree *th;

  /* determine blocksize corresponding to the request psize */
  i=find_blocksize(psize);
  /* find hole of size 2^i */
  th= get_hole(i);  /* returns a pointer to a BTree leaf-node */
  /* add info about new proc. allocation */
  th->type=proc;
  th->psize=psize;
  /* add info about proc. to proc-table */
  pt[proc-'A']= th;  
}

int find_blocksize(int psize) 
{
  int i;

  /* if size request is <= smallest block size, use this block-size */
  if (psize <= pow(2,l))
    return l;
  /* otherwise, find i such that 2^{i-1} < psize <= 2^i */
  i=u;
  while (psize <= pow(2,i-1)) i--;
  return i;
}

BTree *get_hole(int i) {
  List *h1, *h2;
  BTree *tmp;

  if (i==(u+1)) 
    exit(1);    /* this should never happen with a correct input!*/
  
  if (holes[i]==NULL) { /* if there is no hole of size 2^i,      */
    tmp=get_hole(i+1);  /* look for one of size 2^(i+1) to split */
    /* splitting */
    /* at tree-level: split buddy of size 2^(i+1) in two buddies*/ 
    tmp->left= new_node(HOLE,0,tmp->bsize-1,tmp);
    tmp->right=new_node(HOLE,0,tmp->bsize-1,tmp);
    tmp->type= NODE;
    tmp->psize= 0;
    /* add the two buddies to the holes list at entry i */
    /* make hole[i]= h1 --> h2 --> hole[i]              */
    h1= (List *) malloc(sizeof(List));
    h2= (List *) malloc(sizeof(List));
    h1->t= tmp->left;
    h2->t= tmp->right;
    h2->next= holes[i];
    h1->next= h2;
    holes[i]= h1;
  }
  tmp= holes[i]->t; /* return the hole of size 2^i      */
                    /* remove it from the holes[i] list */
  holes[i]= holes[i]->next;

  return (tmp);
}

void unallocate_block(char proc) {
  BTree *t,*p;
  List *l;

  /* find corresponding leaf-node from proc-table */
  t= pt[proc-'A'];
  /* make it a free block */
  t->type= HOLE;
  t->psize= 0;
  /* if it has a companion free buddy, merge them into a larger block */
  p=t->parent;
  if (p->left->type == HOLE && p->right->type == HOLE) 
    merge_buddies(p,t);
  else { /* otherwise, add the freed block to the holes list */
    l=(List *) malloc(sizeof(List));
    l->t= t;
    l->next=holes[t->bsize];
    holes[t->bsize]=l;
  }
  /* mark process as unallocated in the proc-table */
  pt[proc-'A']= NULL;
}

void  merge_buddies(BTree *p, BTree *t)
{
  BTree *newp;
  List *l,*h,*h1;

  /* the parent changes from NODE to HOLE */
  p->type=HOLE;
  /* remove the buddies from the holes list; note that
     the most recent buddy is not there yet (so only one is removed)! */
  h=holes[t->bsize];
  h1=h;
  /* search in the holes list for the buddy that is a child of p */
  while ((h->t != p->left) && (h->t != p->right)) {
    h1=h;
    h=h->next;
  }
  h1->next=h->next;  /* remove it from the list */
  /* free node holes from BTree */
  free(p->left);
  free(p->right);
  /* add merged buddies to holes list as a larger block */
  l=(List *) malloc(sizeof(List));
  l->t= p;                /* larger block corresponds to parent */
  l->next=holes[p->bsize];
  holes[p->bsize]=l;
  newp=p->parent;
  /* recurse if this new buddy hole has a companion buddy hole */
  if (newp!=NULL && newp->left->type == HOLE && newp->right->type== HOLE) 
    merge_buddies(newp,p);
}

BTree *new_node(int type,int psize,int bsize,BTree *par) {
  BTree *t= (BTree *) malloc(sizeof(BTree));
  
  t->type=type;
  t->psize=psize;
  t->bsize=bsize;
  t->parent=par;
  return(t);
}

void print_buddy(BTree *t) 
{
  /* simply a depth-first tree traversal */
  if (t->type==NODE) {
    print_buddy(t->left);
    print_buddy(t->right);
  }
  else if (t->type==HOLE) 
    printf("Hole:%d\n",(int)pow(2,t->bsize));
  else /* proc */
    printf("%c:%d\n",t->type,t->psize);
}
