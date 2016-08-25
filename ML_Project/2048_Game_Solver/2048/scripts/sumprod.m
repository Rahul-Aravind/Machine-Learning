function [b_i, b_ij, logZ] = sumprod(V, X, E, adj, w_i, w_ij, its)
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

% Beliefs & Approximate Partition function
[b_i, b_ij] = beliefs(V, X, E, adj, phi, psi, its);
logZ = -bfe(V, X, E, w_i, w_ij, b_i, b_ij);
end


function [b_i, b_ij] = beliefs(V, X, E, adj, phi, psi, its)
% Loopy message update
m = cell(V, V);
for ij = E'
    i = ij(1);
    j = ij(2);
    m{i, j} = rand(1, X(j));
    m{j, i} = rand(1, X(i));
end
for t = 1:its
    prev_m = m;
    m = cell(V, V);
    for ij = E'
        i = ij(1);
        j = ij(2);
        m{i, j} = zeros(1, X(j));
        m{j, i} = zeros(1, X(i));
        [~, N_i] = find(adj(i, :));
        [~, N_j] = find(adj(j, :));
        sum_ij = 0;
        sum_ji = 0;
        for x_j = 1:X(j)
            m{i, j}(x_j) = msg(X, N_i, phi, psi, prev_m, i, j, x_j);
            sum_ij = sum_ij + m{i, j}(x_j);
        end
        for x_i = 1:X(i)
            m{j, i}(x_i) = msg(X, N_j, phi, psi, prev_m, j, i, x_i);
            sum_ji = sum_ji + m{j, i}(x_i);
        end
        m{i, j} = m{i, j} / sum_ij;
        m{j, i} = m{j, i} / sum_ji;
    end
end

% Belief calculation of the nodes
b_i = cell(1, V);
for i = 1:V
    b_i{i} = zeros(1, X(i));
    [~, N_i] = find(adj(i, :));
    sum = 0;
    for x_i = 1:X(i)
        product = 1;
        for k = N_i
            product = product * m{k, i}(x_i);
        end
        b_i{i}(x_i) = phi{i}(x_i) * product;
        sum = sum + b_i{i}(x_i);
    end
    b_i{i} = b_i{i} / sum;
end

% Belief calculation of the edges
b_ij = cell(V, V);
for ij = E'
    i = ij(1);
    j = ij(2);
    b_ij{i, j} = zeros(X(i), X(j));
    [~, N_i] = find(adj(i, :));
    [~, N_j] = find(adj(j, :));
    sum = 0;
    for x_i = 1:X(i)
        for x_j = 1:X(j)
            product = 1;
            for k = N_i
                if k ~= j
                    product = product * m{k, i}(x_i);
                end
            end
            for k = N_j
                if k ~= i
                    product = product * m{k, j}(x_j);
                end
            end
            b_ij{i, j}(x_i, x_j) = phi{i}(x_i) * phi{j}(x_j) * psi{i, j}(x_i, x_j) * product;
            sum = sum + b_ij{i, j}(x_i, x_j);
        end
    end
    b_ij{i, j} = b_ij{i, j} / sum;
    b_ij{j, i} = b_ij{i, j}';
end
end


function m = msg(X, N_i, phi, psi, prev_m, i, j, x_j)
% Message passed by a single node for all values X
m = 0;
for x_i = 1:X(i)
    product = 1;
    for k = N_i
        if (k ~= j)
            product = product * prev_m{k, i}(x_i);
        end
    end
    m = m + phi{i}(x_i) * psi{i, j}(x_i, x_j) * product;
end
end


function Fb = bfe(V, X, E, log_phi, log_psi, T_i, T_ij)
% Bethe Free Energy
Fb = 0;
for i = 1:V
    for x_i = 1:X(i)
        t_i = T_i{i}(x_i);
        if t_i > 0
            Fb = Fb - t_i * (log_phi{i}(x_i) - log(t_i));
        end
    end
end

for ij = E'
    i = ij(1);
    j = ij(2);
    for x_i = 1:X(i)
        for x_j = 1:X(j)
            t_i = T_i{i}(x_i);
            t_j = T_i{j}(x_j);
            t_ij = T_ij{i, j}(x_i, x_j);
            if t_ij > 0
                Fb = Fb - t_ij * (log_psi{i, j}(x_i, x_j) - log(t_ij/(t_i*t_j)));
            end
        end
    end
end
end