function [w_i, w_ij] = hommle(V, X, E, adj, samples)
% Empirical count
[M, N_i, N_ij] = count(V, X, E, samples);

% Smoothing to avoid zero probabilities
%N_i = N_i + 1;
%N_ij = N_ij + 1;

% Iterations for BP
its = 100;

% Initial Potential functions
w_i = cell(1, V);
w_ij = cell(V, V);
for i = 1:V
    w_i{i} = zeros(1, X(i));
end
for ij = E'
    i = ij(1);
    j = ij(2);
    w_ij{i, j} = zeros(X(i), X(j));
    w_ij{j, i} = zeros(X(j), X(i));
end
[b_i, b_ij, logZ] = sumprod(V, X, E, adj, w_i, w_ij, its);
ll0 = LL(V, E, M, N_i, N_ij, w_i, w_ij, logZ);

% Perform Gradient Ascent
t = 0;
while 1
    gamma = 2 / (2+t);
    % Single gradient step towards positive peak
    [w_i, w_ij] = grad_step(V, X, E, M, w_i, w_ij, N_i, N_ij, b_i, b_ij, gamma);
    
    [b_i, b_ij, logZ] = sumprod(V, X, E, adj, w_i, w_ij, its);
    ll1 = LL(V, E, M, N_i, N_ij, w_i, w_ij, logZ);
    if ll1 - ll0 < 0
        % Convergence condition: No significant increase in the log-likelihood
        break;
    end
    ll0 = ll1;
end
end


function [M, N_i, N_ij] = count(V, X, E, samples)
% Returns the size and count of the variables observed in the samples
M = size(samples, 1);
N_i = cell(1, V);
for i = 1:V
    N_i{i} = zeros(1, X(i));
    for xi = 1:X(i)
        N_i{i}(xi) = size(find(samples(:, i)==xi), 1);
    end
end
N_ij = cell(V, V);
for ij = E'
    i = ij(1);
    j = ij(2);
    N_ij{i, j} = zeros(X(i), X(j));
    for xi = 1:X(i)
        for xj = 1:X(j)
            N_ij{i, j}(xi, xj) = size(find(samples(:, i)==xi & samples(:, j)==xj), 1);
        end
    end
    N_ij{j, i} = N_ij{i, j}';
end
end


function [w_i, w_ij] = grad_step(V, X, E, M, w_i, w_ij, N_i, N_ij, b_i, b_ij, gamma)
for i = 1:V
    w_i{i} = zeros(1, X(i));
    for xi = 1:X(i)
        % Partial derivative of Log Likelihood wrt w_{i,x_i}
        w_i{i}(xi) = w_i{i}(xi) + gamma * N_i{i}(xi) - M * b_i{i}(xi);
    end
end
for ij = E'
    i = ij(1);
    j = ij(2);
    w_ij{i, j} = zeros(X(i), X(j));
    for xi = 1:X(i)
        for xj = 1:X(j)
            % Partial derivative of Log Likelihood wrt w_{i,j,x_i,x_j}
            w_ij{i, j}(xi, xj) = w_ij{i, j}(xi, xj) + gamma * N_ij{i, j}(xi, xj) - M * b_ij{i, j}(xi, xj);
        end
    end
    w_ij{j, i} = w_ij{i, j}';
end

% Normalize the weights
for i = 1:V
    % w_i{i} = w_i{i} - min(w_i{i});
    max_w_i = max(w_i{i});
    if max_w_i > 0
        w_i{i} = w_i{i} / max_w_i;
    end
end
for ij = E'
    i = ij(1);
    j = ij(2);
    % min_w_ij = min(min(w_ij{i, j}));
    % w_ij{i, j} = w_ij{i, j} - min_w_ij;
    % w_ij{j, i} = w_ij{j, i} - min_w_ij;
    max_w_ij = max(max(w_ij{i, j}));
    if max_w_ij > 0
        w_ij{i, j} = w_ij{i, j} / max_w_ij;
    end
    w_ij{j, i} = w_ij{i, j}';
end
end


function ll = LL(V, E, M, N_i, N_ij, w_i, w_ij, logZ)
% Log likelihood
ll = 0;
for i = 1:V
    ll = sum(N_i{i} .* w_i{i});
end
for ij = E'
    i = ij(1);
    j = ij(2);
    ll = sum(sum(N_ij{i, j} .* w_ij{i, j}));
end
ll = ll - M * logZ;
end