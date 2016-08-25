function [dmst, parents] = struct_learn(X)
% Learn the structure of the Bayesian Network using Chow-Liu Trees
W = mutual_info(X);
% Negate the mutual info/weights to get Max Spanning Tree using Min Spanning Tree function
maxW = max(W(:)) + 1;
UG = tril(sparse(maxW - W));
root = 17; % Col 17 is the label/root
[mst, parents] = graphminspantree(UG, root);
[~, m] = size(X);
W = zeros(1, m); % Weights of the Min Spanning Tree
for i = 1:m
    if parents(i) == 0
        W(i) = 0;
    elseif ~isempty(nonzeros(mst(parents(i), i)))
        W(i) = nonzeros(mst(parents(i), i));
    else
        W(i) = nonzeros(mst(i, parents(i)));
    end
end
W = maxW - W; % Weights of the Max Spanning Tree
parents(parents == 0) = root;
dmst = sparse(parents, 1:m, W);
view(biograph(dmst, {}, 'ShowArrows', 'on', 'ShowWeights', 'on'));
parents(root) = 0;
end

function I = mutual_info(X)
% Returns the mutual information between every pair of attributes
[n, m] = size(X);
I = zeros(m, m);
for i = 1:m
    Ui = unique(X(:,i));
    Ni = histc(X(:,i), Ui);
    Xi100 = X(:,i) * 100; % (xij = xi * 100 + xj) to get unique pairs of (xi,xj)
    for j = 1:i-1
        % Mutual information is symmetric, I(xi,xj) = I(xj,xi)
        Uj = unique(X(:,j));
        Nj = histc(X(:,j), Uj);
        Xij = Xi100 + X(:,j);
        Uij = unique(Xij);
        Nij = histc(Xij, Uij);
        w = 0;
        for xi = Ui'
            ni = Ni(Ui == xi);
            for xj = Uj'
                nj = Nj(Uj == xj);
                nij = Nij(Uij == (xi * 100 + xj));
                if ~isempty(nij)
                    w = w + nij * log((nij*n) / (ni*nj));
                end
            end
        end
        I(i,j) = w / n; % Divide by 'N' to normalize for probability
    end
end
end