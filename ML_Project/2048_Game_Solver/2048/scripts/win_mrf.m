function p = win_mrf(x, phi, psi)
% Returns the conditional probability of the assignment 'y' given 'x'
j = 17;
p = exp(phi{j});
for i = 1:16
    p = p .* exp(psi{i, j}(x(i), :));
end
%p = p / sum(p);
p = p(2);
end