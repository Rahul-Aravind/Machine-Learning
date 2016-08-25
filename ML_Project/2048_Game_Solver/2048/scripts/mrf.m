function [acc] = mrf()
V = 17; % Number of tiles in the board + Label (W/L)
X = [12 * ones(1, 16), 2]; % Number of values from 2^0 to 2^11
adj = zeros(17, 17); % Edges in the MRF structure
for i = 1:4
    for j = 1:4
        adj(sub2ind([4,4], j, i), 17) = 1;
        adj(17, sub2ind([4,4], j, i)) = 1;
        if i-1 > 0
            adj(sub2ind([4,4], j, i), sub2ind([4,4], j, i-1)) = 1;
        end
        if i+1 < 5
            adj(sub2ind([4,4], j, i), sub2ind([4,4], j, i+1)) = 1;
        end
        if j-1 > 0
            adj(sub2ind([4,4], j, i), sub2ind([4,4], j-1, i)) = 1;
        end
        if j+1 < 5
            adj(sub2ind([4,4], j, i), sub2ind([4,4], j+1, i)) = 1;
        end
    end
end
[v, u] = ind2sub(size(adj), find(tril(adj)==1));
E = [u, v];

% Learn the parameters
win = read_data('win-train.txt');
win = [win, 2*ones(size(win, 1), 1)];
loss = read_data('loss-test.txt');
loss = [loss, 1*ones(size(loss, 1), 1)];
[w_i, w_ij] = hommle(V, X, E, adj, [win; loss]);
save MRF w_i w_ij;

% Potential functions
phi = cell(size(w_i));
for i = 1:V
    phi{i} = exp(w_i{i});
end
psi = cell(size(w_ij));
for ij = E'
    i = ij(1);
    j = ij(2);
    psi{i, j} = exp(w_ij{i, j});
    psi{j, i} = psi{i, j}';
end

% Test the classification accuracy
X_w = read_data('win-test.txt');
Y_w = 2 * ones(size(X_w, 1), 1);
X_l = read_data('loss-test.txt');
Y_l = ones(size(X_l, 1), 1);
acc = test(phi, psi, [X_w; X_l], [Y_w; Y_l]);
end


function samples = read_data(file_name)
samples = importdata(file_name, ' ');
samples = samples + 1; % Easy for counting 0's
end


function acc = test(phi, psi, X, Y)
% Test the accuracy of the classifier with probabilities Py & Pxy
n = size(X, 1);
correct = 0;
for i = 1:n
    p = cond_dist(X(i, 1:16), phi, psi);
    [~, y] = max(p);
    if y == Y(i)
        correct = correct + 1;
    end
end
acc = correct / n;
end


function p = cond_dist(x, phi, psi)
% Returns the conditional probability of the assignment 'y' given 'x'
j = 17;
p = phi{j};
for i = 1:16
    p = p .* psi{i, j}(x(i), :);
end
end