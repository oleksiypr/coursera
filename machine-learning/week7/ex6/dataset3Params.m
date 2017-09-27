function [C, sigma] = dataset3Params(X, y, Xval, yval)
%DATASET3PARAMS returns your choice of C and sigma for Part 3 of the exercise
%where you select the optimal (C, sigma) learning parameters to use for SVM
%with RBF kernel
%   [C, sigma] = DATASET3PARAMS(X, y, Xval, yval) returns your choice of C and 
%   sigma. You should complete this function to return the optimal C and 
%   sigma based on a cross-validation set.
%

% You need to return the following variables correctly.
C = 1;
sigma = 0.3;

% ====================== YOUR CODE HERE ======================
% Instructions: Fill in this function to return the optimal C and sigma
%               learning parameters found using the cross validation set.
%               You can use svmPredict to predict the labels on the cross
%               validation set. For example, 
%                   predictions = svmPredict(model, Xval);
%               will return the predictions on the cross validation set.
%
%  Note: You can compute the prediction error using 
%        mean(double(predictions ~= yval))
%

values = [0.01 0.03 0.1 0.3 1 3 10 30];
K = length(values);

c_min = 0;
s_min = 0;
err_min = 2.0;

for c = 1: K
   for s = 1: K 
       C = values(c);
       sigma = values(s);       
       model = svmTrain(X, y, C, ...
           @(x1, x2) gaussianKernel(x1, x2, sigma));
       
       predictions = svmPredict(model, Xval); 
       err = mean(double(predictions ~= yval));       
            
       if err < err_min 
           err_min = err;
           c_min = c;
           s_min = s;
       end
   end 
end

disp("----------")
disp([c_min s_min]);

C = values(c_min);
sigma = values(s_min);

% =========================================================================
end
