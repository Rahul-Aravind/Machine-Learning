function move = move_mrf(x, phi, psi)
% Test the accuracy of the classifier with probabilities Py & Pxy
p = cond_dist(x(1:16), phi, psi);
[~, move] = max(p);
end


function p = cond_dist(x, phi, psi)
% Returns the conditional probability of the assignment 'y' given 'x'
j = 17;
p = phi{j};
for i = 1:16
    p = p .* psi{i, j}(x(i), :);
end
end