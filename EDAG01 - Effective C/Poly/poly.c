#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#include <math.h>
#include "poly.h"
#include "error.h"

// Supposed to work as a linked list
struct poly_t {
    int coefficient;
    int exponential;
    poly_t* next;
};

void node(poly_t* poly){
    poly_t* p = malloc(sizeof(poly_t*));
    poly->next = p;
    p->next = NULL;
    p->coefficient = 0;
    p->exponential = 0;
}

// Creates polynomial expression from string (stdin)
// poly_t* new_poly_from_string(const char* xx)
// {
// 	char * ch = malloc((strlen(xx)+1)*sizeof(char));
// 	strcpy(ch, xx);
// 	int sign = 1;
// 	poly_t* poly = malloc(sizeof(poly_t*));
// 	poly->coefficient = 0;
// 	poly->exponential = 0;
// 	poly_t* head = poly; // is to be returned

// 	int add = 0;
// 	char* term = strtok(ch," ");
// 	if (xx[0] == '-') {
// 		sign = -1;
// 		add = 1;
// 	}

// 	while (term != NULL) {
// 		int x, nbr = 0, i_count = 0 + add, x_found = 0;
// 		int coeff = 1, exponent = 0, exp_found = 0;

// 		while ((x = term[i_count++])) {

// 			if (isdigit(x))
// 				nbr = nbr*10 + x -'0';

// 			switch (x) {
// 				case 'x':
// 					x_found = 1;
// 					if (nbr)
// 						coeff = nbr;

// 					nbr = 0;
// 					exponent = 1;
// 					break;

// 				case '^':
// 					exp_found = 1;
// 					break;

// 				case '-':
// 					sign = -1;
// 					coeff = 0;
// 					break;

// 				case '+':
// 					sign = 1;
// 					coeff = 0;
// 					break;
// 			}
// 			add = 0;
// 		}

// 		if (exp_found)
// 			exponent = nbr;

// 		if (!exp_found && !x_found && nbr)
// 			coeff = nbr;

// 		if (coeff) {
// 			poly->coefficient = sign*coeff;
// 			poly->exponential = exponent;
// 			node(poly);
// 			poly = poly->next;
// 		}

// 		term = strtok(NULL, " ");
// 	}

// 	free(ch);
// 	return head;
// }

poly_t* new_poly_from_string(const char* new_poly){
    char* ch = malloc((strlen(new_poly)+1*sizeof(char)));
    strcpy(ch, new_poly);
    int polarity = 1; // 1 means positive and -1 means negative expression
    
    // Create our polynomial
    poly_t* poly = malloc(sizeof(poly_t*));
    poly->coefficient = 0;
    poly->exponential = 0;
    poly_t* head = poly; // First element of list

    int add = 0; // Means to add/subtract further on
    char* term = strtok(ch, " ");
    if(new_poly[0] == '-'){
        polarity = -1;
        add = 1;
    }

    while(term != NULL){
        int x;
        int nbr = 0;
        int i_count = 0 + add;
        int x_found = 0;
        int coefficient = 1;
        int exponent = 0;
        int exp_found = 0;

        while((x = term[i_count++])){
            if(isdigit(x)){
                nbr = nbr*10 + x -'0'; // Nbr from ASCII to number
            }

            switch (x){
                case 'x':
                    x_found = 1;
                    if(nbr){
                        coefficient = nbr;
                    }
                    nbr = 0;
                    exponent = 1;
                    break;
                
                case '+':
                    polarity = 1;
                    coefficient = 0;
                    break;

                case '-':
                    polarity = -1;
                    coefficient = 0;
                    break;
                
                case '^':
                    exp_found = 1;
                    break;
            }

            add = 0;
        }

        // Only exponent
        if(exp_found){
            exponent = nbr;
        }

        // No exponent, no x, but number is found
        if(!exp_found && !x_found && nbr){
            coefficient = nbr;
        }

        if(coefficient){
            poly->coefficient = polarity*coefficient;
            poly->exponential = exponent;
            node(poly);
            poly = poly->next; // Next element in list created in node(), empty node
        }

        term = strtok(NULL, " ");
    }
    free(ch);
    return head;
}

// Frees one polynomial
void free_poly(poly_t* poly){
	while (poly) {
		poly_t* p = poly;
		poly = poly->next;
		free(p);
	}
	poly = NULL;
}

// Polynomial multiplication
poly_t* mul(poly_t* p1, poly_t* p2){
    poly_t* result = malloc(sizeof(poly_t*));
    poly_t* head_result = result;
    poly_t* head_second = p2;

    int single = 0;

    while(p1){
        int coefficient = p1->coefficient;
        int exponent = p1->exponential;

        while(p2){
            int coefficient_2 = p2->coefficient;
            int exponent_2 = p2->exponential;

            // If anyone of the coefficients are zero, then skip this
            if(coefficient && coefficient_2){
                int result_coefficient = coefficient * coefficient_2;
                int result_exponent = exponent + exponent_2;

                poly_t* prev_result = head_result;
                poly_t* temp_result = head_result->next;
                
                if(!single){
                    temp_result = NULL;
                    single = 1;
                }

                while(temp_result != NULL){
                    // Same exponent, add the coefficients toghether
                    if(prev_result->exponential == result_exponent){
                        prev_result->coefficient += result_coefficient;
                        break;
                    }

                    if(result_exponent > temp_result->exponential){
                        node(prev_result);
                        prev_result = prev_result->next;
                        prev_result->exponential = result_exponent;
                        prev_result->coefficient = result_coefficient;
                        prev_result->next = temp_result;
                        break;
                    }

                    temp_result = temp_result->next;
                    prev_result = prev_result->next;
                    if(temp_result == NULL){
                        break;
                    }
                }

                // No polynomial of same degree found...
                if(temp_result == NULL){
                    node(result);
                    result->coefficient = result_coefficient;
                    result->exponential = result_exponent;
                    result = result->next;
                }
            }

            p2 = p2->next;
        }

        p2 = head_second;
        p1 = p1->next;
    }

    return head_result; 
}

// Prints out the polynomial
void print_poly(poly_t* poly){
    poly_t* temp = poly;

    int more_iterations = 0;
    while(temp){
        if(more_iterations){
            if(temp->coefficient < 0){
                printf(" - ");
            }else if(temp->coefficient > 0){
                printf(" + ");
            }
        // Negative sign and coefficient at the start
        }else if(!more_iterations && temp->coefficient < 0){
            printf("-");
        }

        int coefficient = temp->coefficient;
        int exponent = temp->exponential;

        // If exponent = 0 then there is nothing to show exponentialy, if exponent = 1 then its just a normal x polynomial expression
        if(exponent != 0 && exponent != 1){
            if(coefficient == 1 || coefficient == -1){
                printf("x^%d", exponent);
            }else{
                printf("%dx^%d", abs(coefficient), exponent);
            }
        // Plain x polynomial expression
        }else if(exponent == 1){
            if(coefficient == 1 || coefficient == -1){
                printf("x");
            }else{
                printf("%dx", abs(coefficient));
            }
        // Only number
        }else if(exponent == 0 && coefficient){
            printf("%d", abs(coefficient));
        }

        temp = temp->next;
        more_iterations = 1;
    }
    printf("\n");
}