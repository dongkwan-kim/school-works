/* todokaist */
#include <stdio.h>
#include "cachelab.h"
#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

/* structure */
typedef struct {
	int valid;
	int tag;
	int used_time;
}cache_line;

typedef struct {
	cache_line *lines;
}cache_set;

typedef struct {
	cache_set *sets;
}cache_sim;

/* proto type */
void cache_on(cache_sim *my_cache, int s, int E);
int get_set_bits(int addr, int s, int b);
int get_tag_bits(int addr, int s, int b);
int get_behav(cache_sim *my_cache, char inst, int addr, int s, int E, int b);
int find_LRU_index(cache_set *my_set, int E);
void prt_behav(char *f_line, int behav);
void del_line_break(char *line);

/* global var */
int g_hit;
int g_miss;
int g_evic;
int g_time = 0;

int main(int argc, char **argv){	
	cache_sim my_cache;
	int flag;
	int v_opt, s_opt, E_opt, b_opt;
	FILE *f_opt;

	char f_line[20], inst;
	int addr;

	int behav;
	
	/* command parsing */
	while((flag = getopt(argc, argv, "vs:E:b:t:")) != -1){
		switch(flag){
			case 'v':
				v_opt = 1;
				break;
			case 's':
				s_opt = atoi(optarg);
				break;
			case 'E':
				E_opt = atoi(optarg);
				break;
			case 'b':
				b_opt = atoi(optarg);
				break;
			case 't':
				f_opt = fopen(optarg,"r");
				break;
		}		
	}
	/* memory allocation && set all valid bits 0*/
	cache_on(&my_cache, s_opt, E_opt);
    
	/* read file: t- [file name] */
	while(fgets(f_line, 20, f_opt) != NULL){
		
		/* ignore instruction I */
		if(f_line[0] != ' '){
			continue;
		}

		/* get instruction and address */
		sscanf(f_line, " %c %x,", &inst, &addr);

		/* run cache */
		behav = get_behav(&my_cache, inst, addr, s_opt, E_opt, b_opt);
		
		/* verbose output */
		if(v_opt == 1){
			prt_behav(f_line, behav);
		}
	}
	printSummary(g_hit, g_miss, g_evic);
	fclose(f_opt);
    return 0;
}

int get_behav(cache_sim *my_cache, char inst, int addr, int s, int E, int b){
	int tag_n, set_i, line_i;
	cache_set my_set;
	int hit_flag, evic_flag, M_flag, sum_flag;
	int num_valids;

	hit_flag = 0;
	evic_flag = 0;
	M_flag = 0;

	num_valids = 0;

	tag_n = get_tag_bits(addr, s, b);
	set_i = get_set_bits(addr, s, b);
	my_set = my_cache->sets[set_i];
	
	/* time increases on each line */
	g_time++;

	/* hit: right valid, right tag */
	for(line_i = 0; line_i < E; line_i++){
		if(my_set.lines[line_i].valid == 1){
			num_valids++;	
			if(my_set.lines[line_i].tag == tag_n){
				g_hit++;
				hit_flag = 1;
				my_set.lines[line_i].used_time = g_time;
			}
		}
	}
	
	/* miss: set valid = 1, set tag, update time */
	if(!hit_flag){
		g_miss++;
		line_i = find_LRU_index(&my_set, E);
		my_set.lines[line_i].valid = 1;
		my_set.lines[line_i].tag = tag_n;
		my_set.lines[line_i].used_time = g_time;
	}
	
	/* eviction: every valid tag in the set is 1 */
	if((!hit_flag) && (num_valids == E)){
		g_evic++;
		evic_flag = 2;
	}

	/* instruction M (always) hits again */
	if(inst == 'M'){
		g_hit++;
		M_flag = 3;
	}
	
	/* miss: 0, hit: 1, miss_eviction: 2, miss_hit: 3
	 *		    hit_hit: 4, miss_eviction_hit: 5 */
	sum_flag = hit_flag + evic_flag + M_flag;
	return sum_flag;
}

int find_LRU_index(cache_set *my_set, int E){
	/* If there is an empty line, return it
	 * Else return Least Recently Used line*/
	int line_i, LRU_i;

	if(my_set->lines[0].valid == 0){
		return 0;
	} else { 
		LRU_i = 0;
	}
	for(line_i = 0; line_i < E; line_i++){
		if(my_set->lines[line_i].valid == 0){
			return line_i;
		}
		if(my_set->lines[line_i].used_time < my_set->lines[LRU_i].used_time){
			LRU_i = line_i;
		}
	}
	return LRU_i;
}

void prt_behav(char *f_line, int behav){
	/* miss: 0, hit: 1, miss_eviction: 2, miss_hit: 3,
	   hit_hit: 4, miss_eviction_hit: 5 */
	del_line_break(f_line);
	switch(behav){
		case 0:
			printf("%s%s\n", f_line, "miss");
			break;
		case 1:	
			printf("%s%s\n", f_line, "hit");
			break;
		case 2:
			printf("%s%s\n", f_line, "miss eviction");
			break;
		case 3:
			printf("%s%s\n", f_line, "miss hit");
			break;
		case 4:
			printf("%s%s\n", f_line, "hit hit");
			break;
		case 5:
			printf("%s%s\n", f_line, "miss eviction hit");
			break;
	}		
}

void cache_on(cache_sim *my_cache, int s, int E){
	int set_i, line_i, S;
	S = 2 << s;
	my_cache->sets = (cache_set *)malloc(S * sizeof(cache_set));
	for(set_i = 0; set_i < S; set_i++){
		my_cache->sets[set_i].lines = (cache_line *)malloc(E * sizeof(cache_line));
		for(line_i = 0; line_i < E; line_i++){
			my_cache->sets[set_i].lines[line_i].valid = 0;
		}
	}
}

int get_set_bits(int addr, int s, int b){
	unsigned mask;
	mask = (~0)+(1<<s);
	return (addr>>b)&mask;
}

int get_tag_bits(int addr, int s, int b){
	unsigned mask;
	mask = (~0)+(1<<(32-s-b));
	return (addr>>(s+b))&mask;
}

void del_line_break(char *line){
	int index, len;
	len = strlen(line);
	for (index = 0; index < len; index++){
		if (line[index] == '\n'){
			line[index] = ' ';
		}
	}
}
