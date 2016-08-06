/*
* Used Segregated free list: 16, 32, 64, 128, 256, 512, inf

* Free block structure
* |head|prev|next|padding|foot| at least 4Byte
*      ▲__ pointer
                                
* Allocated block structure
* |head|payload and padding|foot|

*/

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <unistd.h>
#include <string.h>

#include "mm.h"
#include "memlib.h"

/*********************************************************
 * NOTE TO STUDENTS: Before you do anything else, please
 * provide your team information in the following struct.
 ********************************************************/
team_t team = {
    /* Team name */
    "todo",
    /* First member's full name */
    "todokaist",
    /* First member's email address */
    "todokaist@gmail.com",
    /* Second member's full name (leave blank if none) */
    "",
    /* Second member's email address (leave blank if none) */
    ""
};

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)

#define SIZE_T_SIZE (ALIGN(sizeof(size_t)))

/* Basic constants and macros */
#define WSIZE 4
#define DSIZE 8
#define CHUNKSIZE (1<<12)

#define max(x, y) ((x) > (y)? (x) : (y))

/* Pack a size and allocated bit into a word */
#define pack(size, alloc) ((size) | (alloc))

/* Read and write a word at address p */
#define get(p) (*(unsigned int *)(p))
#define put(p, val) (*(unsigned int *)(p) = (val))

/* Read the size and allocated fields from address p */
#define get_size(p) (get(p) & ~0x7)
#define get_alloc(p) (get(p) & 0x1)

/* Given block ptr bp, compute address of its header adn */
#define hdrp(p) ((char *)(p) - WSIZE)
#define ftrp(p) ((char *)(p) + get_size(hdrp(p)) - DSIZE)

/* Given block ptr bp, comput address of next and previous blocks */
#define next_blkp(bp) ((char *)(bp) + get_size(((char *)(bp) - WSIZE)))
#define prev_blkp(bp) ((char *)(bp) - get_size(((char *)(bp) - DSIZE)))

/* For free blocks, node manipulation  */
/* [Free block] |head|prev|next|padding|foot| at least 4B */
/*                   ▲__ pointer                          */
#define get_prev_node(p) (get((char *)(p)))
#define get_next_node(p) (get((char *)(p) + WSIZE))
#define put_prev_node(p, prev) (put((char *)(p), prev))
#define put_next_node(p, next) (put((char *)(p) + WSIZE, next))

/* Gloval variable */
void *heap_listp;
void *seg16 = NULL, *seg32 = NULL, *seg64 = NULL, *seg128 = NULL, *seg256 = NULL, *seg512 = NULL, *seginf = NULL;
void *end16 = NULL, *end32 = NULL, *end64 = NULL, *end128 = NULL, *end256 = NULL, *end512 = NULL, *endinf = NULL;

/*help function proto type*/
static void *extend_heap(size_t words);
static void *coalesce(void *bp);
static void *find_first_fit_in_seglist(size_t asize);
static void *find_best_fit_in_seglist(size_t asize);
static void place(void *bp, size_t asize);

/* 
 * Seglist Functions
 * */

/* add node to the given seglist */
void add_node(void **seg, void **end, void *new_node){
	if (*seg == NULL) { /* empty */
		*seg = new_node;
		*end = new_node;
		put_prev_node(new_node, new_node);
		put_next_node(new_node, new_node);
	} else {
		put_prev_node(new_node, *end);
		put_next_node(*end, new_node);
		put_next_node(new_node, new_node);
		*end = new_node;
	}
	return;
}

/* delete node in the given seglist */
void delete_node(void **seg, void **end, void *del_node){
	if((del_node == get_prev_node(del_node))
		&& (del_node == get_next_node(del_node))){
		*seg = NULL;
		*end = NULL;
	} else if (del_node == get_prev_node(del_node)) {
		*seg = get_next_node(*seg);
		put_prev_node(*seg, *seg);
	} else if (del_node == get_next_node(del_node)) {
		*end = get_prev_node(del_node);
		put_next_node(*end, *end);
	} else {
		void *temp_prev, *temp_next;
		temp_prev = get_prev_node(del_node);
		temp_next = get_next_node(del_node);
		put_prev_node(temp_next, temp_prev);
		put_next_node(temp_prev, temp_next);
	}
	return;
}

/* add node to the proper seglist */
void delete_node_with_size(void *del_node, size_t size){
	if (size <= 16) {
		delete_node(&seg16, &end16, del_node);
	} else if (size <= 32) {
		delete_node(&seg32, &end32, del_node);
	} else if (size <= 64) {
		delete_node(&seg64, &end64, del_node);
	} else if (size <= 128) {
		delete_node(&seg128, &end128, del_node);
	} else if (size <= 256) {
		delete_node(&seg256, &end256, del_node);
	} else if (size <= 512) {
		delete_node(&seg512, &end512, del_node);
	} else {
		delete_node(&seginf, &endinf, del_node);
	}
}

/* delete node in the proper seglist */
void add_node_with_size(void *new_node, size_t size){
	if (size <= 16) {
		add_node(&seg16, &end16, new_node);
	} else if (size <= 32) {
		add_node(&seg32, &end32, new_node);
	} else if (size <= 64) {
		add_node(&seg64, &end64, new_node);
	} else if (size <= 128) {
		add_node(&seg128, &end128, new_node);
	} else if (size <= 256) {
		add_node(&seg256, &end256, new_node);
	} else if (size <= 512) {
		add_node(&seg512, &end512, new_node);
	} else {
		add_node(&seginf, &endinf, new_node);
	}
}

/* search node of the given size and return it */
void *search_node_with_size(size_t size){
	if (size <= 16) {
		return seg16;
	} else if (size <= 32) {
		return seg32;
	} else if (size <= 64) {
		return seg64;
	} else if (size <= 128) {
		return seg128;
	} else if (size <= 256) {
		return seg256;
	} else if (size <= 512) {
		return seg512;
	} else {
		return seginf;
	}
}
/* End */


/* 
 * mm_init - initialize the malloc package.
 */
int mm_init(void)
{
	if((heap_listp = mem_sbrk(4*WSIZE) ) == (void *) -1){    
		return -1;
	}
	put(heap_listp, 0);
	put(heap_listp + (1*WSIZE), pack(DSIZE, 1));
	put(heap_listp + (2*WSIZE), pack(DSIZE, 1));
	put(heap_listp + (3*WSIZE), pack(0, 1)); 
	
	heap_listp += 2*WSIZE;
		
	if(extend_heap(CHUNKSIZE/WSIZE) == NULL){
		return -1;
	}
	return 0;
}

/* make heap larger */
static void *extend_heap(size_t words)
{
	char *bp;
	size_t size;
	
	size = (words % 2) ? (words + 1) * WSIZE : words * WSIZE;

	if((long)(bp = mem_sbrk(size)) == -1){
		return NULL;
	}
	put(hdrp(bp), pack(size, 0));
	put(ftrp(bp), pack(size, 0));
	put(hdrp(next_blkp(bp)), pack(0, 1));
	add_node_with_size(bp, size);

	return coalesce(bp);
}

/* 
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
	size_t asize;
	size_t extendsize;
	char *bp;

	if(size <= 0){
		return NULL;
	}
	/* min of size is 2*DSIZE = 16B = head + prev + next + foot */
	if(size <= DSIZE){	
		asize = 2*DSIZE;
	} else {
		asize = DSIZE * ((size + (2*DSIZE-1)) / DSIZE); /* align 8 */
	}
	
	/* Search the free list */
	if((bp = find_best_fit_in_seglist(asize)) != NULL){
		place(bp, asize);
		return bp;
	}						
	
	/* If there is proper fit, extend heap */
	extendsize = max(asize, CHUNKSIZE);
	if((bp = extend_heap(extendsize/WSIZE)) == NULL){
		return NULL;
	}
    place(bp, asize);
	return bp;
}

/* find first fit in seglist, if there is not, return NULL */
static void *find_first_fit_in_seglist(size_t asize){

	void *seg, *bp;
	int seg_cont, list_cont;
	
	seg_cont = 1;
	seg = search_node_with_size(asize);
	while(seg_cont){
		bp = seg;
		list_cont = 1;
		while(list_cont){
			if(bp == NULL) {
				break;
			}

			if(!get_alloc(hdrp(bp)) && asize <= get_size(hdrp(bp))){
				return bp;
			}
			
			/* If bp is the last node, break while loop */
			if(bp == get_next_node(bp)){
				list_cont = 0;
			} else {
				bp = get_next_node(bp);
			}
		}

		/* If seg is the last seglist, break while loop */
		if(seg == seginf){
			seg_cont = 0;
		} else{
			asize = asize * 2;
			seg = search_node_with_size(asize);
		}
	}
	return NULL;
}


/* find best fit in seglist, if there is not, return NULL */
static void *find_best_fit_in_seglist(size_t asize){
	void *seg, *bp, *bestp;
	int seg_cont, list_cont, best_size;
	seg_cont = 1;
	seg = search_node_with_size(asize);
	
	while(seg_cont){
		bp = seg;
		list_cont = 1;
		best_size = 1<<30;
		bestp = NULL;
		while(list_cont){
			if(bp == NULL) {
				break;
			}

			if(!get_alloc(hdrp(bp)) && asize <= get_size(hdrp(bp))){
				
				if(get_size(hdrp(bp)) < best_size){
					bestp = bp;
					best_size = get_size(hdrp(bp));
				}
			}
			
			/* If bp is the last node, break while loop */
			if(bp == get_next_node(bp)){
				list_cont = 0;
			} else {
				bp = get_next_node(bp);
			}
		}
		if(bestp != NULL){
			return bestp;
		}
		/* If seg is the last seglist, break while loop */
		if(seg == seginf){
			seg_cont = 0;
		} else{
			asize = asize * 2;
			seg = search_node_with_size(asize);
		}
	}
	return NULL;
}

/* place the block on the given position */
static void place(void *bp, size_t asize){

	size_t csize = get_size(hdrp(bp));
	delete_node_with_size(bp, csize);
	
	if((csize - asize) >= (2*DSIZE)){
		put(hdrp(bp), pack(asize, 1));
		put(ftrp(bp), pack(asize, 1));
		bp = next_blkp(bp);
		put(hdrp(bp), pack(csize-asize, 0));								
		put(ftrp(bp), pack(csize-asize, 0));
		add_node_with_size(bp, csize - asize);
	} else {
		put(hdrp(bp), pack(csize, 1));
		put(ftrp(bp), pack(csize, 1));
    }
}

/*
 * mm_free - Freeing a block does nothing.
 */
void mm_free(void *ptr)
{
	size_t size = get_size(hdrp(ptr));
	put(hdrp(ptr), pack(size, 0));
	put(ftrp(ptr), pack(size, 0));
	add_node_with_size(ptr, size);
	coalesce(ptr);
}

/* coalesce the adjacent blocks */
static void *coalesce(void *bp)
{
	size_t prev_alloc = get_alloc(ftrp(prev_blkp(bp)));
	size_t next_alloc = get_alloc(hdrp(next_blkp(bp)));
	size_t size = get_size(hdrp(bp));

	if(prev_alloc && next_alloc){
		return bp;
	}
	else if(prev_alloc && !next_alloc){
		delete_node_with_size(bp, size);
		delete_node_with_size(next_blkp(bp), get_size(hdrp(next_blkp(bp))));
		size += get_size(hdrp(next_blkp(bp)));
		put(hdrp(bp), pack(size, 0));
		put(ftrp(bp), pack(size, 0));
		add_node_with_size(bp, size);
	}
	else if(!prev_alloc && next_alloc){
		delete_node_with_size(bp, size);
		delete_node_with_size(prev_blkp(bp), get_size(hdrp(prev_blkp(bp))));
		size += get_size(hdrp(prev_blkp(bp)));
		put(ftrp(bp), pack(size, 0));
		put(hdrp(prev_blkp(bp)), pack(size, 0));
		bp = prev_blkp(bp);
		add_node_with_size(bp, size);
	}
	else {
		delete_node_with_size(bp, size);
		delete_node_with_size(prev_blkp(bp), get_size(hdrp(prev_blkp(bp))));
		delete_node_with_size(next_blkp(bp), get_size(hdrp(next_blkp(bp))));
		size += get_size(hdrp(prev_blkp(bp))) + get_size(ftrp(next_blkp(bp)));
		put(hdrp(prev_blkp(bp)), pack(size, 0));
		put(ftrp(next_blkp(bp)), pack(size, 0));
		bp = prev_blkp(bp);
		add_node_with_size(bp, size);
	}
	return bp;
}


/*
 * mm_realloc - Implemented simply in terms of mm_malloc and mm_free
 */
void *mm_realloc(void *ptr, size_t size)
{
    void *oldptr = ptr;
    void *newptr;
    size_t copySize;
	size_t newSize;

	if (ptr == NULL){
		return mm_malloc(size);
	}	
	if (size == 0){
		mm_free(oldptr);
		return 0;
	}
	copySize = get_size(hdrp(ptr));
	newSize = ALIGN(size + 2*DSIZE);
	
	if (newSize <= copySize){
		return ptr;
	}
	
	size_t nblkp_alloc = get_alloc(hdrp(next_blkp(ptr)));
	size_t size_of_2 = copySize + get_size(hdrp(next_blkp(ptr)));
	
	if (!nblkp_alloc && size_of_2 >= newSize){
		delete_node_with_size(next_blkp(ptr), get_size(hdrp(next_blkp(ptr))));
		put(hdrp(ptr), pack(size_of_2, 1));
		put(ftrp(ptr), pack(size_of_2, 1));
		return ptr;
	} else {
		newptr = mm_malloc(size);
		if (newptr == NULL)
			return NULL;
		if (size < copySize)
			copySize = size;
    
		memcpy(newptr, oldptr, copySize);
		mm_free(oldptr);
		return newptr;
	}
}

/* Heap consistency checker */
int mm_check(void){
	size_t size;
	void *seg;
	int flag;
	int check_1, check_2;
	flag = 1;
	check_1 = 1;
	check_2 = 1;
	for(size = 16; size <= 1024; size *= 2){
		seg = search_node_with_size(size);
		while(flag){
			if(seg == NULL){
				break;
			}
			
			/* Is every block in the free list marked as free? */
			if(get_alloc(hdrp(seg))){
				printf("This block %x is not marked as free!\n", seg);	
				check_1 = 0;
				break;
			}

			/* Are there any contiguous free blocks that somehow escaped coalecing? */
			if(!get_alloc(hdrp(next_blkp(seg)))){
				printf("Contiguous free blocks %x, %x are escaped colescing!\n", seg, next_blkp(seg));
				check_2 = 0;
				break;
			}
			
			if(seg == get_next_node(seg)){
				flag = 0;
			} else {
				seg = get_next_node(seg);
			}
		}
	}
	
	if(check_1){
		printf("Every block in the free list makred as free\n");
	}
	if(check_2){
		printf("Every free block is coalesced\n");
	}
}
