function p = win_gbn(x, Py, Pxy, parents)
% Returns the conditional probability of the assignment 'y' given 'x'
N_y = find(parents == 17);
p = Py;
for i = N_y
    p(1) = p(1) .* Pxy{i, 1}(x(i));
    p(2) = p(2) .* Pxy{i, 2}(x(i));
end
p = p / sum(p);
p = p(2);
end