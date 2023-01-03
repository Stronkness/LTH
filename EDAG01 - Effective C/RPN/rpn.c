#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define N (10)


static void error(unsigned int line, int c, bool* err) {
    char buf[3];
    if (c == '\n') 
        strcpy(buf, "\\n");
    else {
        buf[0] = c;
        buf[1] = 0;
    }
    printf("line %u: error at %s\n", line, buf);

    *err = true;
}

int main (void) {
    int         stack [N];
    int         i;
    int         c;
    //int         d;
    int         x; //value of number
    bool        num; //reading a number
    bool        err; // found error on line
    unsigned    line; //line number

    x = 0;
    i = 0;
    line = 1;
    num = false;
    err = false;

    while((c = getchar()) != EOF) {
        error:
        if (err) {

            while(c != '\n') c = getchar();

            if(c == '\n') {
                line += 1;
                err = 0;
                i = 0;
            }
            continue;
        } else if(isdigit(c)) {
            x = x * 10 + c - '0'; // Base 10
            num = true;
            continue;
        } else if(num) {
            if (i == N){
                error(line, '0' + x%10, &err);
                continue;
            }
            else {
               stack[i++] = x;
               num = false;
               x = 0;
               
            }
        }
        
        // Constraints
        if(c == ' '){
            continue;
        }else if(c == '!'){
            error(line, c, &err);
            continue;
        }else if(c == '+' || c == '-' || c == '*' || c == '/'){
            if(i < 2){
                error(line, c, &err);
                continue;
            }

            int calc;
            int temp;
            switch (c){
            case '+':
                calc = stack[i-2] + stack[i-1];
                stack[i-2] = calc;
                i = i-1;
                break;
            case '-':
                calc = stack[i-2] - stack[i-1];
                stack[i-2] = calc;
                i = i-1;
                break;
            case '/':
                temp = stack[i-1];
                if(temp == 0){
                    error(line, c, &err);
                    continue;
                }
                calc = stack[i-2] / stack[i-1];
                stack[i-2] = calc;
                i = i-1;
                break;
            case '*':
                calc = stack[i-2] * stack[i-1];
                stack[i-2] = calc;
                i = i-1;
                break;

            default:
                error(line, c, &err);
                break;
            }

        }else if(c == '\n' && i == 2){
            error(line, c, &err);
            goto error;
            continue;
        }else if (c == '\n'){
            if(i > 0){
                printf("line %u: %d\n", line, stack[0]);
                line += 1;
                err = 0;
                x = 0;
                i = 0;
                num = false;
                continue;
            }else{
                error(line, c, &err);
                goto error;
            }
        }
    }
}