function [] = test(x)
x = x+1;

load MRF;
win_mrf(x, w_i, w_ij)

load NB;
win_gbn(x, Py, Pxy, parents)

load BN;
win_gbn(x, Py, Pxy, parents)

load BN2;
win_gbn(x, Py, Pxy, parents)