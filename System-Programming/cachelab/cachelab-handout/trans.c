/* todokaist */
/* 
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */ 
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/* 
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded. 
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{	
	int ifac, jfac, i, j, tmp, k, l;
	int s0, s1, s2, s3;
	
	/* -M 32 -N 32 */
	if(M == 32){
		/* 8X8 square : row 4, col 4 */
		for (ifac = 0; ifac < 4; ifac++){
			for (jfac = 0; jfac < 4; jfac++){
				for (i = 8 * ifac; i < 8 * ifac + 8; i++){
					tmp = A[i][i - 8 * ifac + 8 * jfac];
					for (j = 8 * jfac; j < 8 * jfac + 8; j++){
						if ((i - 8 * ifac) != (j - 8 * jfac)){
							B[j][i] = A[i][j];
						}
					}
					B[i - 8 * ifac + 8 * jfac][i] = tmp;
				}
			}
		}
	}

	/* -M 64 -N 64 */
	if (M == 64){
		/* 8X8 square : row 8, col 8 */
		for (ifac = 0; ifac < 8; ifac++){
			for (jfac = 0; jfac < 8; jfac++){
				/* not diagonal */
				if (ifac != jfac){
					for (i = 8 * ifac; i < 8 * ifac + 4; i++){
						for (j = 8 * jfac; j < 8 * jfac + 4; j++){
							B[j][i] = A[i][j];
						}
					}
					for (i = 8 * ifac; i < 8 * ifac + 4; i++){
						for (j = 8 * jfac + 4; j < 8 * jfac + 8; j++){
							B[j - 4][i + 4] = A[i][j];
						}
					}
					for (i = 8 * ifac; i < 8 * ifac + 4; i++){
						s0 = B[8 * jfac + (i - 8 * ifac)][8 * ifac + 4];
						s1 = B[8 * jfac + (i - 8 * ifac)][8 * ifac + 5];
						s2 = B[8 * jfac + (i - 8 * ifac)][8 * ifac + 6];
						s3 = B[8 * jfac + (i - 8 * ifac)][8 * ifac + 7];						
						for(k = 4; k < 8; k++){
							B[8 * jfac + (i - 8 * ifac)][8 * ifac + k] = A[8 * ifac + k][8 * jfac + (i - 8 * ifac)];
						}
						B[8 * jfac + 4 + (i - 8 * ifac)][8 * ifac] = s0;
						B[8 * jfac + 4 + (i - 8 * ifac)][8 * ifac + 1] = s1;
						B[8 * jfac + 4 + (i - 8 * ifac)][8 * ifac + 2] = s2;
						B[8 * jfac + 4 + (i - 8 * ifac)][8 * ifac + 3] = s3;
					}
					for (i = 8 * ifac + 4; i < 8 * ifac + 8; i++){
						for (j = 8 * jfac + 4; j < 8 * jfac + 8; j++){
							B[j][i] = A[i][j];
						}
					}
				}
				/* diagonal */
				else {
					for(l = 0; l <= 4; l = l + 4){
						for(k = 0; k <= 4; k = k + 4){
							for (i = 8 * ifac + k; i < 8 * ifac + k + 4; i++){
								tmp = A[i][i - (8 * ifac + k) + (8 * jfac + l)];
								for (j = 8 * jfac + l; j < 8 * jfac + l + 4; j++){
									if ((i - 8 * ifac - k) != (j - 8 * jfac - l)){
										B[j][i] = A[i][j];
									}
								}
								B[i - (8 * ifac + k) + (8 * jfac + l)][i] = tmp;
							}		
						}
					}
				}
			}
		}
	}
	
	/* -M 61 -N 67 */
	if (M == 61){
		for (ifac = 0; ifac < 9; ifac++){
			for (jfac = 0; jfac < 8; jfac++){
				for (j = 8 * jfac; j < 8 * jfac + 8 && j < 61; j++){
					for (i = 8 * ifac; i < 8 * ifac + 8 && i < 67; i++){
						B[j][i] = A[i][j];
					}
				}
			}
		}
	}
}

/* 
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started. 
 */ 

/* 
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; j++) {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }    

}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc); 

    /* Register any additional transpose functions */
    registerTransFunction(trans, trans_desc); 

}

/* 
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}

