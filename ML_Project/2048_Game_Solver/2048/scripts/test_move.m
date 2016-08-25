function [] = test_move(x)
x = x+1;

load MRF_move;
move_mrf(x, w_i, w_ij)

load NB_move;
move_gbn(x, Py, Pxy, parents)

load BN_move;
move_gbn(x, Py, Pxy, parents)

load BN2_move;
move_gbn(x, Py, Pxy, parents)