#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#include <math.h>
#include "poly.h"

struct poly_t {
	int coeff;
	int expo;
	poly_t* next;
};

void new_node(poly_t* poly)
{
	poly_t* p = malloc(sizeof(poly_t*));
	poly->next = p;
	p->next = NULL;
	p->coeff = 0;
	p->expo = 0;
}

/* Creates a polynomial of a string */
poly_t* new_poly_from_string(const char* xx)
{
	char * ch = malloc((strlen(xx)+1)*sizeof(char));
	strcpy(ch, xx);
	int sign = 1;
	poly_t* poly = malloc(sizeof(poly_t*));
	poly->coeff = 0;
	poly->expo = 0;
	poly_t* head = poly; // is to be returned

	int add = 0;
	char* term = strtok(ch," ");
	if (xx[0] == '-') {
		sign = -1;
		add = 1;
	}

	while (term != NULL) {
		int x, nbr = 0, i_count = 0 + add, x_found = 0;
		int coeff = 1, exponent = 0, exp_found = 0;

		while ((x = term[i_count++])) {

			if (isdigit(x))
				nbr = nbr*10 + x -'0';

			switch (x) {
				case 'x':
					x_found = 1;
					if (nbr)
						coeff = nbr;

					nbr = 0;
					exponent = 1;
					break;

				case '^':
					exp_found = 1;
					break;

				case '-':
					sign = -1;
					coeff = 0;
					break;

				case '+':
					sign = 1;
					coeff = 0;
					break;
			}
			add = 0;
		}

		if (exp_found)
			exponent = nbr;

		if (!exp_found && !x_found && nbr)
			coeff = nbr;

		if (coeff) {
			poly->coeff = sign*coeff;
			poly->expo = exponent;
			new_node(poly);
			poly = poly->next;
		}

		term = strtok(NULL, " ");
	}

	free(ch);
	return head;
}

void free_poly(poly_t* poly)
{
	while (poly) {
		poly_t* p = poly;
		poly = poly->next;
		free(p);
	}
	poly = NULL;
}

/* Multiplies two polinomial */
poly_t*	mul(poly_t* poly1, poly_t* poly2)
{
	poly_t* answ = malloc(sizeof(poly_t*));
	poly_t* head_ans = answ; // Save the head
	poly_t* head2 = poly2;
	int singleton = 0;
	while (poly1) {
		int coeffi = poly1->coeff;
		int expoi = poly1->expo;

		while (poly2) {
			int coeffj = poly2->coeff;
			int expoj = poly2->expo;

			/* Proceed if the coefficient is non-zero*/
			if (coeffi && coeffj) {
				int ans_coef = coeffi * coeffj;
				int ans_expo = expoi + expoj;

				/* Look for equal degree */
				poly_t* ans_prev = head_ans;
				poly_t* ans_temp = head_ans->next;
				if (!singleton) {
					ans_temp = NULL;
					singleton = 1;
				}

				while (ans_temp != NULL) {
					if (ans_prev->expo == ans_expo) {
						ans_prev->coeff += ans_coef;
						break;
					}

					if (ans_expo > ans_temp->expo) {
						new_node(ans_prev);
						ans_prev = ans_prev->next;
						ans_prev->expo = ans_expo;
						ans_prev->coeff = ans_coef;
						ans_prev->next = ans_temp;
						break;
					}

					ans_temp = ans_temp->next;
					ans_prev = ans_prev->next;
					if (ans_temp == NULL)
						break;
				}

				/* Has not found a term with the same degree*/
				if (ans_temp == NULL) {
					new_node(answ);
					answ->coeff = ans_coef;
					answ->expo = ans_expo;
					answ = answ->next;
				}
			}
			poly2 = poly2->next;
		}
			poly2 = head2;
			poly1 = poly1->next;
	}

	return head_ans;
}

/* Prints the polynomial */
void print_poly(poly_t* poly)
{
	poly_t* poly2 = poly;

	if (poly2->coeff < 0)
		printf("-");

	while (poly2) {
		int coeff = poly2->coeff;
		int expo = poly2->expo;

		if (expo != 0 && expo != 1) {
			if (coeff == 1 || coeff == -1)
				printf("x^%d",expo);
			else
				printf("%dx^%d",abs(coeff),expo);

		} else if (expo == 1) {
			if (coeff == 1 || coeff == -1)
				printf("x");
			else
				printf("%dx",abs(coeff));

		} else if (expo == 0 && coeff) {
				printf("%d",abs(coeff));
		}

		poly2 = poly2->next;
		if (poly2) {
			if (poly2->coeff < 0)
				printf(" - ");
			else if (poly2->coeff > 0)
				printf(" + ");
		}
	}
	putchar('\n');
}
