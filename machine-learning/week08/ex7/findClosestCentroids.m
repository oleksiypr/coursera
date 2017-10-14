function idx = findClosestCentroids(X, centroids)
%FINDCLOSESTCENTROIDS computes the centroid memberships for every example
%   idx = FINDCLOSESTCENTROIDS (X, centroids) returns the closest centroids
%   in idx for a dataset X where each row is a single example. idx = m x 1 
%   vector of centroid assignments (i.e. each entry in range [1..K])
%

% Set K
K = size(centroids, 1);

% set m
m =  size(X, 1);

% You need to return the following variables correctly.
idx = zeros(m, 1);

% ====================== YOUR CODE HERE ======================
% Instructions: Go over every example, find its closest centroid, and store
%               the index inside idx at the appropriate location.
%               Concretely, idx(i) should contain the index of the centroid
%               closest to example i. Hence, it should be a value in the 
%               range 1..K
%
% Note: You can use a for-loop over the examples to compute this.
%

% matrix of square distances from each point to each centroid
S = zeros(m, K);

for i = 1:m
    for j = 1:K
        dx = X(i, :) - centroids(j, :);
        ds = sum(dx.^2);
        S(i, j) = ds;
    end
end

% search for index of minimum in each row, this index == c(i)
[~, ii] = min(S, [], 2);
idx = ii;

% =============================================================
end

