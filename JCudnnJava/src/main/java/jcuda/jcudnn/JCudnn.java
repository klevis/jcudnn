/*
 * JCudnn - Java bindings for cuDNN, the NVIDIA CUDA
 * Deep Neural Network library, to be used with JCuda
 *
 * Copyright (c) 2015-2017 Marco Hutter - http://www.jcuda.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package jcuda.jcudnn;

import jcuda.CudaException;
import jcuda.LibUtils;
import jcuda.LogLevel;
import jcuda.Pointer;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaStream_t;

/**
 * Java bindings for cuDNN, the NVIDIA CUDA
 * Deep Neural Network library.
 */
public class JCudnn
{
    public static final int CUDNN_MAJOR      = 7;
    public static final int CUDNN_MINOR      = 4;
    public static final int CUDNN_PATCHLEVEL = 1;

    public static final int CUDNN_VERSION    =
        (CUDNN_MAJOR * 1000 + CUDNN_MINOR * 100 + CUDNN_PATCHLEVEL);
    
    /**
     * Maximum supported number of tensor dimensions 
     */
    public static final int CUDNN_DIM_MAX = 8;
    
    /**
     * Minimum epsilon allowed to be used in the Batch Normalization formula
     */
    public static final double CUDNN_BN_MIN_EPSILON = 1e-5;
    
    
    /** Message masks to be used with cudnnSetCallback() */
    public static final int CUDNN_SEV_ERROR_EN   = (1 << cudnnSeverity.CUDNN_SEV_ERROR);
    public static final int CUDNN_SEV_WARNING_EN = (1 << cudnnSeverity.CUDNN_SEV_WARNING);
    public static final int CUDNN_SEV_INFO_EN    = (1 << cudnnSeverity.CUDNN_SEV_INFO);
    
    /**
     * The flag that indicates whether the native library has been
     * loaded
     */
    private static boolean initialized = false;

    /**
     * Whether a CudaException should be thrown if a method is about
     * to return a result code that is not
     * cudnnStatus.CUDNN_STATUS_SUCCESS
     */
    private static boolean exceptionsEnabled = false;

    /* Private constructor to prevent instantiation */
    private JCudnn()
    {
    }

    // Initialize the native library.
    static
    {
        initialize();
    }

    /**
     * Initializes the native library. Note that this method
     * does not have to be called explicitly, since it will
     * be called automatically when this class is loaded.
     */
    public static void initialize()
    {
        if (!initialized)
        {
            String libraryBaseName = "JCudnn-" + JCuda.getJCudaVersion();
            String libraryName = 
                LibUtils.createPlatformLibraryName(libraryBaseName);
            LibUtils.loadLibrary(libraryName);
            initialized = true;
        }
    }


    /**
     * Enables or disables exceptions. By default, the methods of this class
     * only set the {@link cudnnStatus} from the native methods.
     * If exceptions are enabled, a CudaException with a detailed error
     * message will be thrown if a method is about to set a result code
     * that is not cudnnStatus.CUDNN_STATUS_SUCCESS
     *
     * @param enabled Whether exceptions are enabled
     */
    public static void setExceptionsEnabled(boolean enabled)
    {
        exceptionsEnabled = enabled;
    }

    /**
     * If the given result is not cudnnStatus.CUDNN_STATUS_SUCCESS
     * and exceptions have been enabled, this method will throw a
     * CudaException with an error message that corresponds to the
     * given result code. Otherwise, the given result is simply
     * returned.
     *
     * @param result The result to check
     * @return The result that was given as the parameter
     * @throws CudaException If exceptions have been enabled and
     * the given result code is not cudnnStatus.CUDNN_STATUS_SUCCESS
     */
    private static int checkResult(int result)
    {
        if (exceptionsEnabled && result !=
            cudnnStatus.CUDNN_STATUS_SUCCESS)
        {
            throw new CudaException(cudnnStatus.stringFor(result));
        }
        return result;
    }

    /**
     * Set the specified log level for the JCudnn library.<br />
     * <br />
     * Currently supported log levels:
     * <br />
     * LOG_QUIET: Never print anything <br />
     * LOG_ERROR: Print error messages <br />
     * LOG_TRACE: Print a trace of all native function calls <br />
     *
     * @param logLevel The log level to use.
     */
    public static void setLogLevel(LogLevel logLevel)
    {
        setLogLevelNative(logLevel.ordinal());
    }

    private static native void setLogLevelNative(int logLevel);
    
    
    public static long cudnnGetVersion()
    {
        return cudnnGetVersionNative();
    }
    private static native long cudnnGetVersionNative();


    /** Returns CUDA Runtime version statically linked against cudnn */
    public static long cudnnGetCudartVersion()
    {
        return cudnnGetCudartVersionNative();
    }
    private static native long cudnnGetCudartVersionNative();


    /** human-readable error messages */
    public static String cudnnGetErrorString(
        int status)
    {
        return cudnnGetErrorStringNative(status);
    }
    private static native String cudnnGetErrorStringNative(
        int status);


    public static int cudnnQueryRuntimeError(
        cudnnHandle handle, 
        int[] rstatus, 
        int mode, 
        cudnnRuntimeTag tag)
    {
        return checkResult(cudnnQueryRuntimeErrorNative(handle, rstatus, mode, tag));
    }
    private static native int cudnnQueryRuntimeErrorNative(
        cudnnHandle handle, 
        int[] rstatus, 
        int mode, 
        cudnnRuntimeTag tag);


    public static int cudnnGetProperty(
        int type, 
        int[] value)
    {
        return checkResult(cudnnGetPropertyNative(type, value));
    }
    private static native int cudnnGetPropertyNative(
        int type, 
        int[] value);


    public static int cudnnCreate(
        cudnnHandle handle)
    {
        return checkResult(cudnnCreateNative(handle));
    }
    private static native int cudnnCreateNative(
        cudnnHandle handle);


    public static int cudnnDestroy(
        cudnnHandle handle)
    {
        return checkResult(cudnnDestroyNative(handle));
    }
    private static native int cudnnDestroyNative(
        cudnnHandle handle);


    public static int cudnnSetStream(
        cudnnHandle handle, 
        cudaStream_t streamId)
    {
        return checkResult(cudnnSetStreamNative(handle, streamId));
    }
    private static native int cudnnSetStreamNative(
        cudnnHandle handle, 
        cudaStream_t streamId);


    public static int cudnnGetStream(
        cudnnHandle handle, 
        cudaStream_t streamId)
    {
        return checkResult(cudnnGetStreamNative(handle, streamId));
    }
    private static native int cudnnGetStreamNative(
        cudnnHandle handle, 
        cudaStream_t streamId);


    /** Create an instance of a generic Tensor descriptor */
    public static int cudnnCreateTensorDescriptor(
        cudnnTensorDescriptor tensorDesc)
    {
        return checkResult(cudnnCreateTensorDescriptorNative(tensorDesc));
    }
    private static native int cudnnCreateTensorDescriptorNative(
        cudnnTensorDescriptor tensorDesc);


    public static int cudnnSetTensor4dDescriptor(
        cudnnTensorDescriptor tensorDesc, 
        int format, 
        int dataType, /** image data type */
        int n, /** number of inputs (batch size) */
        int c, /** number of input feature maps */
        int h, /** height of input section */
        int w)/** width of input section */
    {
        return checkResult(cudnnSetTensor4dDescriptorNative(tensorDesc, format, dataType, n, c, h, w));
    }
    private static native int cudnnSetTensor4dDescriptorNative(
        cudnnTensorDescriptor tensorDesc, 
        int format, 
        int dataType, /** image data type */
        int n, /** number of inputs (batch size) */
        int c, /** number of input feature maps */
        int h, /** height of input section */
        int w);/** width of input section */


    public static int cudnnSetTensor4dDescriptorEx(
        cudnnTensorDescriptor tensorDesc, 
        int dataType, /** image data type */
        int n, /** number of inputs (batch size) */
        int c, /** number of input feature maps */
        int h, /** height of input section */
        int w, /** width of input section */
        int nStride, 
        int cStride, 
        int hStride, 
        int wStride)
    {
        return checkResult(cudnnSetTensor4dDescriptorExNative(tensorDesc, dataType, n, c, h, w, nStride, cStride, hStride, wStride));
    }
    private static native int cudnnSetTensor4dDescriptorExNative(
        cudnnTensorDescriptor tensorDesc, 
        int dataType, /** image data type */
        int n, /** number of inputs (batch size) */
        int c, /** number of input feature maps */
        int h, /** height of input section */
        int w, /** width of input section */
        int nStride, 
        int cStride, 
        int hStride, 
        int wStride);


    public static int cudnnGetTensor4dDescriptor(
        cudnnTensorDescriptor tensorDesc, 
        int[] dataType, /** image data type */
        int[] n, /** number of inputs (batch size) */
        int[] c, /** number of input feature maps  */
        int[] h, /** height of input section */
        int[] w, /** width of input section */
        int[] nStride, 
        int[] cStride, 
        int[] hStride, 
        int[] wStride)
    {
        return checkResult(cudnnGetTensor4dDescriptorNative(tensorDesc, dataType, n, c, h, w, nStride, cStride, hStride, wStride));
    }
    private static native int cudnnGetTensor4dDescriptorNative(
        cudnnTensorDescriptor tensorDesc, 
        int[] dataType, /** image data type */
        int[] n, /** number of inputs (batch size) */
        int[] c, /** number of input feature maps  */
        int[] h, /** height of input section */
        int[] w, /** width of input section */
        int[] nStride, 
        int[] cStride, 
        int[] hStride, 
        int[] wStride);


    public static int cudnnSetTensorNdDescriptor(
        cudnnTensorDescriptor tensorDesc, 
        int dataType, 
        int nbDims, 
        int[] dimA, 
        int[] strideA)
    {
        return checkResult(cudnnSetTensorNdDescriptorNative(tensorDesc, dataType, nbDims, dimA, strideA));
    }
    private static native int cudnnSetTensorNdDescriptorNative(
        cudnnTensorDescriptor tensorDesc, 
        int dataType, 
        int nbDims, 
        int[] dimA, 
        int[] strideA);


    public static int cudnnSetTensorNdDescriptorEx(
        cudnnTensorDescriptor tensorDesc, 
        int format, 
        int dataType, 
        int nbDims, 
        int[] dimA)
    {
        return checkResult(cudnnSetTensorNdDescriptorExNative(tensorDesc, format, dataType, nbDims, dimA));
    }
    private static native int cudnnSetTensorNdDescriptorExNative(
        cudnnTensorDescriptor tensorDesc, 
        int format, 
        int dataType, 
        int nbDims, 
        int[] dimA);


    public static int cudnnGetTensorNdDescriptor(
        cudnnTensorDescriptor tensorDesc, 
        int nbDimsRequested, 
        int[] dataType, 
        int[] nbDims, 
        int[] dimA, 
        int[] strideA)
    {
        return checkResult(cudnnGetTensorNdDescriptorNative(tensorDesc, nbDimsRequested, dataType, nbDims, dimA, strideA));
    }
    private static native int cudnnGetTensorNdDescriptorNative(
        cudnnTensorDescriptor tensorDesc, 
        int nbDimsRequested, 
        int[] dataType, 
        int[] nbDims, 
        int[] dimA, 
        int[] strideA);


    public static int cudnnGetTensorSizeInBytes(
        cudnnTensorDescriptor tensorDesc, 
        long[] size)
    {
        return checkResult(cudnnGetTensorSizeInBytesNative(tensorDesc, size));
    }
    private static native int cudnnGetTensorSizeInBytesNative(
        cudnnTensorDescriptor tensorDesc, 
        long[] size);


    /**
     * <pre>
     * PixelOffset( n, c, h, w ) = n *input_stride + c * feature_stride + h * h_stride + w * w_stride
    
       1)Example of all images in row major order one batch of features after the other (with an optional padding on row)
       input_stride :  c x h x h_stride
       feature_stride : h x h_stride
       h_stride  :  >= w  ( h_stride = w if no padding)
       w_stride  : 1
    
    
       2)Example of all images in row major with features maps interleaved
       input_stride :  c x h x h_stride
       feature_stride : 1
       h_stride  :  w x c
       w_stride  : c
    
       3)Example of all images in column major order one batch of features after the other (with optional padding on column)
       input_stride :  c x w x w_stride
       feature_stride : w x w_stride
       h_stride  :  1
       w_stride  :  >= h
    
     * </pre>
     */
    /** Destroy an instance of Tensor4d descriptor */
    public static int cudnnDestroyTensorDescriptor(
        cudnnTensorDescriptor tensorDesc)
    {
        return checkResult(cudnnDestroyTensorDescriptorNative(tensorDesc));
    }
    private static native int cudnnDestroyTensorDescriptorNative(
        cudnnTensorDescriptor tensorDesc);


    /** Tensor layout conversion helper (y = alpha * x + beta * y) */
    public static int cudnnTransformTensor(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnTransformTensorNative(handle, alpha, xDesc, x, beta, yDesc, y));
    }
    private static native int cudnnTransformTensorNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Tensor Bias addition : C = alpha * A + beta * C  */
    public static int cudnnAddTensor(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C)
    {
        return checkResult(cudnnAddTensorNative(handle, alpha, aDesc, A, beta, cDesc, C));
    }
    private static native int cudnnAddTensorNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C);


    public static int cudnnCreateOpTensorDescriptor(
        cudnnOpTensorDescriptor opTensorDesc)
    {
        return checkResult(cudnnCreateOpTensorDescriptorNative(opTensorDesc));
    }
    private static native int cudnnCreateOpTensorDescriptorNative(
        cudnnOpTensorDescriptor opTensorDesc);


    public static int cudnnSetOpTensorDescriptor(
        cudnnOpTensorDescriptor opTensorDesc, 
        int opTensorOp, 
        int opTensorCompType, 
        int opTensorNanOpt)
    {
        return checkResult(cudnnSetOpTensorDescriptorNative(opTensorDesc, opTensorOp, opTensorCompType, opTensorNanOpt));
    }
    private static native int cudnnSetOpTensorDescriptorNative(
        cudnnOpTensorDescriptor opTensorDesc, 
        int opTensorOp, 
        int opTensorCompType, 
        int opTensorNanOpt);


    public static int cudnnGetOpTensorDescriptor(
        cudnnOpTensorDescriptor opTensorDesc, 
        int[] opTensorOp, 
        int[] opTensorCompType, 
        int[] opTensorNanOpt)
    {
        return checkResult(cudnnGetOpTensorDescriptorNative(opTensorDesc, opTensorOp, opTensorCompType, opTensorNanOpt));
    }
    private static native int cudnnGetOpTensorDescriptorNative(
        cudnnOpTensorDescriptor opTensorDesc, 
        int[] opTensorOp, 
        int[] opTensorCompType, 
        int[] opTensorNanOpt);


    public static int cudnnDestroyOpTensorDescriptor(
        cudnnOpTensorDescriptor opTensorDesc)
    {
        return checkResult(cudnnDestroyOpTensorDescriptorNative(opTensorDesc));
    }
    private static native int cudnnDestroyOpTensorDescriptorNative(
        cudnnOpTensorDescriptor opTensorDesc);


    /** Tensor operation : C = op( alpha1 * A, alpha2 * B ) + beta * C */
    /** B tensor is ignored for CUDNN_OP_TENSOR_SQRT, CUDNN_OP_TENSOR_NOT. */
    public static int cudnnOpTensor(
        cudnnHandle handle, 
        cudnnOpTensorDescriptor opTensorDesc, 
        Pointer alpha1, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer alpha2, 
        cudnnTensorDescriptor bDesc, 
        Pointer B, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C)
    {
        return checkResult(cudnnOpTensorNative(handle, opTensorDesc, alpha1, aDesc, A, alpha2, bDesc, B, beta, cDesc, C));
    }
    private static native int cudnnOpTensorNative(
        cudnnHandle handle, 
        cudnnOpTensorDescriptor opTensorDesc, 
        Pointer alpha1, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer alpha2, 
        cudnnTensorDescriptor bDesc, 
        Pointer B, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C);


    public static int cudnnCreateReduceTensorDescriptor(
        cudnnReduceTensorDescriptor reduceTensorDesc)
    {
        return checkResult(cudnnCreateReduceTensorDescriptorNative(reduceTensorDesc));
    }
    private static native int cudnnCreateReduceTensorDescriptorNative(
        cudnnReduceTensorDescriptor reduceTensorDesc);


    public static int cudnnSetReduceTensorDescriptor(
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        int reduceTensorOp, 
        int reduceTensorCompType, 
        int reduceTensorNanOpt, 
        int reduceTensorIndices, 
        int reduceTensorIndicesType)
    {
        return checkResult(cudnnSetReduceTensorDescriptorNative(reduceTensorDesc, reduceTensorOp, reduceTensorCompType, reduceTensorNanOpt, reduceTensorIndices, reduceTensorIndicesType));
    }
    private static native int cudnnSetReduceTensorDescriptorNative(
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        int reduceTensorOp, 
        int reduceTensorCompType, 
        int reduceTensorNanOpt, 
        int reduceTensorIndices, 
        int reduceTensorIndicesType);


    public static int cudnnGetReduceTensorDescriptor(
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        int[] reduceTensorOp, 
        int[] reduceTensorCompType, 
        int[] reduceTensorNanOpt, 
        int[] reduceTensorIndices, 
        int[] reduceTensorIndicesType)
    {
        return checkResult(cudnnGetReduceTensorDescriptorNative(reduceTensorDesc, reduceTensorOp, reduceTensorCompType, reduceTensorNanOpt, reduceTensorIndices, reduceTensorIndicesType));
    }
    private static native int cudnnGetReduceTensorDescriptorNative(
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        int[] reduceTensorOp, 
        int[] reduceTensorCompType, 
        int[] reduceTensorNanOpt, 
        int[] reduceTensorIndices, 
        int[] reduceTensorIndicesType);


    public static int cudnnDestroyReduceTensorDescriptor(
        cudnnReduceTensorDescriptor reduceTensorDesc)
    {
        return checkResult(cudnnDestroyReduceTensorDescriptorNative(reduceTensorDesc));
    }
    private static native int cudnnDestroyReduceTensorDescriptorNative(
        cudnnReduceTensorDescriptor reduceTensorDesc);


    /** Helper function to return the minimum size of the index space to be passed to the reduction given the input and
     * output tensors */
    public static int cudnnGetReductionIndicesSize(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        cudnnTensorDescriptor aDesc, 
        cudnnTensorDescriptor cDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetReductionIndicesSizeNative(handle, reduceTensorDesc, aDesc, cDesc, sizeInBytes));
    }
    private static native int cudnnGetReductionIndicesSizeNative(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        cudnnTensorDescriptor aDesc, 
        cudnnTensorDescriptor cDesc, 
        long[] sizeInBytes);


    /** Helper function to return the minimum size of the workspace to be passed to the reduction given the input and output
     * tensors */
    public static int cudnnGetReductionWorkspaceSize(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        cudnnTensorDescriptor aDesc, 
        cudnnTensorDescriptor cDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetReductionWorkspaceSizeNative(handle, reduceTensorDesc, aDesc, cDesc, sizeInBytes));
    }
    private static native int cudnnGetReductionWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        cudnnTensorDescriptor aDesc, 
        cudnnTensorDescriptor cDesc, 
        long[] sizeInBytes);


    /** Tensor operation : C = reduce op( alpha * A ) + beta * C */
    /** The NaN propagation enum applies to only the min and max reduce ops; the other reduce ops propagate NaN as usual. */
    /** The indices space is ignored for reduce ops other than min or max. */
    public static int cudnnReduceTensor(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        Pointer indices, 
        long indicesSizeInBytes, 
        Pointer workspace, 
        long workspaceSizeInBytes, 
        Pointer alpha, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C)
    {
        return checkResult(cudnnReduceTensorNative(handle, reduceTensorDesc, indices, indicesSizeInBytes, workspace, workspaceSizeInBytes, alpha, aDesc, A, beta, cDesc, C));
    }
    private static native int cudnnReduceTensorNative(
        cudnnHandle handle, 
        cudnnReduceTensorDescriptor reduceTensorDesc, 
        Pointer indices, 
        long indicesSizeInBytes, 
        Pointer workspace, 
        long workspaceSizeInBytes, 
        Pointer alpha, 
        cudnnTensorDescriptor aDesc, 
        Pointer A, 
        Pointer beta, 
        cudnnTensorDescriptor cDesc, 
        Pointer C);


    /** Set all values of a tensor to a given value : y[i] = value[0] */
    public static int cudnnSetTensor(
        cudnnHandle handle, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        Pointer valuePtr)
    {
        return checkResult(cudnnSetTensorNative(handle, yDesc, y, valuePtr));
    }
    private static native int cudnnSetTensorNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        Pointer valuePtr);


    /** Scale all values of a tensor by a given factor : y[i] = alpha * y[i] */
    public static int cudnnScaleTensor(
        cudnnHandle handle, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        Pointer alpha)
    {
        return checkResult(cudnnScaleTensorNative(handle, yDesc, y, alpha));
    }
    private static native int cudnnScaleTensorNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        Pointer alpha);


    /** Create an instance of FilterStruct */
    public static int cudnnCreateFilterDescriptor(
        cudnnFilterDescriptor filterDesc)
    {
        return checkResult(cudnnCreateFilterDescriptorNative(filterDesc));
    }
    private static native int cudnnCreateFilterDescriptorNative(
        cudnnFilterDescriptor filterDesc);


    public static int cudnnSetFilter4dDescriptor(
        cudnnFilterDescriptor filterDesc, 
        int dataType, /** image data type */
        int format, 
        int k, /** number of output feature maps */
        int c, /** number of input feature maps */
        int h, /** height of each input filter */
        int w)/** width of  each input filter */
    {
        return checkResult(cudnnSetFilter4dDescriptorNative(filterDesc, dataType, format, k, c, h, w));
    }
    private static native int cudnnSetFilter4dDescriptorNative(
        cudnnFilterDescriptor filterDesc, 
        int dataType, /** image data type */
        int format, 
        int k, /** number of output feature maps */
        int c, /** number of input feature maps */
        int h, /** height of each input filter */
        int w);/** width of  each input filter */


    public static int cudnnGetFilter4dDescriptor(
        cudnnFilterDescriptor filterDesc, 
        int[] dataType, /** image data type */
        int[] format, 
        int[] k, /** number of output feature maps */
        int[] c, /** number of input feature maps */
        int[] h, /** height of each input filter */
        int[] w)/** width of  each input filter */
    {
        return checkResult(cudnnGetFilter4dDescriptorNative(filterDesc, dataType, format, k, c, h, w));
    }
    private static native int cudnnGetFilter4dDescriptorNative(
        cudnnFilterDescriptor filterDesc, 
        int[] dataType, /** image data type */
        int[] format, 
        int[] k, /** number of output feature maps */
        int[] c, /** number of input feature maps */
        int[] h, /** height of each input filter */
        int[] w);/** width of  each input filter */


    public static int cudnnSetFilterNdDescriptor(
        cudnnFilterDescriptor filterDesc, 
        int dataType, /** image data type */
        int format, 
        int nbDims, 
        int[] filterDimA)
    {
        return checkResult(cudnnSetFilterNdDescriptorNative(filterDesc, dataType, format, nbDims, filterDimA));
    }
    private static native int cudnnSetFilterNdDescriptorNative(
        cudnnFilterDescriptor filterDesc, 
        int dataType, /** image data type */
        int format, 
        int nbDims, 
        int[] filterDimA);


    public static int cudnnGetFilterNdDescriptor(
        cudnnFilterDescriptor filterDesc, 
        int nbDimsRequested, 
        int[] dataType, /** image data type */
        int[] format, 
        int[] nbDims, 
        int[] filterDimA)
    {
        return checkResult(cudnnGetFilterNdDescriptorNative(filterDesc, nbDimsRequested, dataType, format, nbDims, filterDimA));
    }
    private static native int cudnnGetFilterNdDescriptorNative(
        cudnnFilterDescriptor filterDesc, 
        int nbDimsRequested, 
        int[] dataType, /** image data type */
        int[] format, 
        int[] nbDims, 
        int[] filterDimA);


    public static int cudnnDestroyFilterDescriptor(
        cudnnFilterDescriptor filterDesc)
    {
        return checkResult(cudnnDestroyFilterDescriptorNative(filterDesc));
    }
    private static native int cudnnDestroyFilterDescriptorNative(
        cudnnFilterDescriptor filterDesc);


    /** Create an instance of convolution descriptor */
    public static int cudnnCreateConvolutionDescriptor(
        cudnnConvolutionDescriptor convDesc)
    {
        return checkResult(cudnnCreateConvolutionDescriptorNative(convDesc));
    }
    private static native int cudnnCreateConvolutionDescriptorNative(
        cudnnConvolutionDescriptor convDesc);


    public static int cudnnSetConvolutionMathType(
        cudnnConvolutionDescriptor convDesc, 
        int mathType)
    {
        return checkResult(cudnnSetConvolutionMathTypeNative(convDesc, mathType));
    }
    private static native int cudnnSetConvolutionMathTypeNative(
        cudnnConvolutionDescriptor convDesc, 
        int mathType);


    public static int cudnnGetConvolutionMathType(
        cudnnConvolutionDescriptor convDesc, 
        int[] mathType)
    {
        return checkResult(cudnnGetConvolutionMathTypeNative(convDesc, mathType));
    }
    private static native int cudnnGetConvolutionMathTypeNative(
        cudnnConvolutionDescriptor convDesc, 
        int[] mathType);


    public static int cudnnSetConvolutionGroupCount(
        cudnnConvolutionDescriptor convDesc, 
        int groupCount)
    {
        return checkResult(cudnnSetConvolutionGroupCountNative(convDesc, groupCount));
    }
    private static native int cudnnSetConvolutionGroupCountNative(
        cudnnConvolutionDescriptor convDesc, 
        int groupCount);


    public static int cudnnGetConvolutionGroupCount(
        cudnnConvolutionDescriptor convDesc, 
        int[] groupCount)
    {
        return checkResult(cudnnGetConvolutionGroupCountNative(convDesc, groupCount));
    }
    private static native int cudnnGetConvolutionGroupCountNative(
        cudnnConvolutionDescriptor convDesc, 
        int[] groupCount);


    public static int cudnnSetConvolution2dDescriptor(
        cudnnConvolutionDescriptor convDesc, 
        int pad_h, /** zero-padding height */
        int pad_w, /** zero-padding width */
        int u, /** vertical filter stride */
        int v, /** horizontal filter stride */
        int dilation_h, /** filter dilation in the vertical dimension */
        int dilation_w, /** filter dilation in the horizontal dimension */
        int mode, 
        int computeType)
    {
        return checkResult(cudnnSetConvolution2dDescriptorNative(convDesc, pad_h, pad_w, u, v, dilation_h, dilation_w, mode, computeType));
    }
    private static native int cudnnSetConvolution2dDescriptorNative(
        cudnnConvolutionDescriptor convDesc, 
        int pad_h, /** zero-padding height */
        int pad_w, /** zero-padding width */
        int u, /** vertical filter stride */
        int v, /** horizontal filter stride */
        int dilation_h, /** filter dilation in the vertical dimension */
        int dilation_w, /** filter dilation in the horizontal dimension */
        int mode, 
        int computeType);


    public static int cudnnGetConvolution2dDescriptor(
        cudnnConvolutionDescriptor convDesc, 
        int[] pad_h, /** zero-padding height */
        int[] pad_w, /** zero-padding width */
        int[] u, /** vertical filter stride */
        int[] v, /** horizontal filter stride */
        int[] dilation_h, /** filter dilation in the vertical dimension */
        int[] dilation_w, /** filter dilation in the horizontal dimension */
        int[] mode, 
        int[] computeType)
    {
        return checkResult(cudnnGetConvolution2dDescriptorNative(convDesc, pad_h, pad_w, u, v, dilation_h, dilation_w, mode, computeType));
    }
    private static native int cudnnGetConvolution2dDescriptorNative(
        cudnnConvolutionDescriptor convDesc, 
        int[] pad_h, /** zero-padding height */
        int[] pad_w, /** zero-padding width */
        int[] u, /** vertical filter stride */
        int[] v, /** horizontal filter stride */
        int[] dilation_h, /** filter dilation in the vertical dimension */
        int[] dilation_w, /** filter dilation in the horizontal dimension */
        int[] mode, 
        int[] computeType);


    /** Helper function to return the dimensions of the output tensor given a convolution descriptor */
    public static int cudnnGetConvolution2dForwardOutputDim(
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        cudnnFilterDescriptor filterDesc, 
        int[] n, 
        int[] c, 
        int[] h, 
        int[] w)
    {
        return checkResult(cudnnGetConvolution2dForwardOutputDimNative(convDesc, inputTensorDesc, filterDesc, n, c, h, w));
    }
    private static native int cudnnGetConvolution2dForwardOutputDimNative(
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        cudnnFilterDescriptor filterDesc, 
        int[] n, 
        int[] c, 
        int[] h, 
        int[] w);


    public static int cudnnSetConvolutionNdDescriptor(
        cudnnConvolutionDescriptor convDesc, 
        int arrayLength, /** nbDims-2 size */
        int[] padA, 
        int[] filterStrideA, 
        int[] dilationA, 
        int mode, 
        int computeType)/** convolution data type */
    {
        return checkResult(cudnnSetConvolutionNdDescriptorNative(convDesc, arrayLength, padA, filterStrideA, dilationA, mode, computeType));
    }
    private static native int cudnnSetConvolutionNdDescriptorNative(
        cudnnConvolutionDescriptor convDesc, 
        int arrayLength, /** nbDims-2 size */
        int[] padA, 
        int[] filterStrideA, 
        int[] dilationA, 
        int mode, 
        int computeType);/** convolution data type */


    public static int cudnnGetConvolutionNdDescriptor(
        cudnnConvolutionDescriptor convDesc, 
        int arrayLengthRequested, 
        int[] arrayLength, 
        int[] padA, 
        int[] strideA, 
        int[] dilationA, 
        int[] mode, 
        int[] computeType)/** convolution data type */
    {
        return checkResult(cudnnGetConvolutionNdDescriptorNative(convDesc, arrayLengthRequested, arrayLength, padA, strideA, dilationA, mode, computeType));
    }
    private static native int cudnnGetConvolutionNdDescriptorNative(
        cudnnConvolutionDescriptor convDesc, 
        int arrayLengthRequested, 
        int[] arrayLength, 
        int[] padA, 
        int[] strideA, 
        int[] dilationA, 
        int[] mode, 
        int[] computeType);/** convolution data type */


    /** Helper function to return the dimensions of the output tensor given a convolution descriptor */
    public static int cudnnGetConvolutionNdForwardOutputDim(
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        cudnnFilterDescriptor filterDesc, 
        int nbDims, 
        int[] tensorOuputDimA)
    {
        return checkResult(cudnnGetConvolutionNdForwardOutputDimNative(convDesc, inputTensorDesc, filterDesc, nbDims, tensorOuputDimA));
    }
    private static native int cudnnGetConvolutionNdForwardOutputDimNative(
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        cudnnFilterDescriptor filterDesc, 
        int nbDims, 
        int[] tensorOuputDimA);


    /** Destroy an instance of convolution descriptor */
    public static int cudnnDestroyConvolutionDescriptor(
        cudnnConvolutionDescriptor convDesc)
    {
        return checkResult(cudnnDestroyConvolutionDescriptorNative(convDesc));
    }
    private static native int cudnnDestroyConvolutionDescriptorNative(
        cudnnConvolutionDescriptor convDesc);


    public static int cudnnGetConvolutionForwardAlgorithmMaxCount(
        cudnnHandle handle, 
        int[] count)
    {
        return checkResult(cudnnGetConvolutionForwardAlgorithmMaxCountNative(handle, count));
    }
    private static native int cudnnGetConvolutionForwardAlgorithmMaxCountNative(
        cudnnHandle handle, 
        int[] count);


    public static int cudnnFindConvolutionForwardAlgorithm(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults)
    {
        return checkResult(cudnnFindConvolutionForwardAlgorithmNative(handle, xDesc, wDesc, convDesc, yDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnFindConvolutionForwardAlgorithmNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults);


    public static int cudnnFindConvolutionForwardAlgorithmEx(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnFindConvolutionForwardAlgorithmExNative(handle, xDesc, x, wDesc, w, convDesc, yDesc, y, requestedAlgoCount, returnedAlgoCount, perfResults, workSpace, workSpaceSizeInBytes));
    }
    private static native int cudnnFindConvolutionForwardAlgorithmExNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes);


    public static int cudnnGetConvolutionForwardAlgorithm(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo)
    {
        return checkResult(cudnnGetConvolutionForwardAlgorithmNative(handle, xDesc, wDesc, convDesc, yDesc, preference, memoryLimitInBytes, algo));
    }
    private static native int cudnnGetConvolutionForwardAlgorithmNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo);


    public static int cudnnGetConvolutionForwardAlgorithm_v7(
        cudnnHandle handle, 
        cudnnTensorDescriptor srcDesc, 
        cudnnFilterDescriptor filterDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor destDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults)
    {
        return checkResult(cudnnGetConvolutionForwardAlgorithm_v7Native(handle, srcDesc, filterDesc, convDesc, destDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnGetConvolutionForwardAlgorithm_v7Native(
        cudnnHandle handle, 
        cudnnTensorDescriptor srcDesc, 
        cudnnFilterDescriptor filterDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor destDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionFwdAlgoPerf[] perfResults);


    /**
     *  convolution algorithm (which requires potentially some workspace)
     */
    /** Helper function to return the minimum size of the workspace to be passed to the convolution given an algo*/
    public static int cudnnGetConvolutionForwardWorkspaceSize(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int algo, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetConvolutionForwardWorkspaceSizeNative(handle, xDesc, wDesc, convDesc, yDesc, algo, sizeInBytes));
    }
    private static native int cudnnGetConvolutionForwardWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor yDesc, 
        int algo, 
        long[] sizeInBytes);


    /** Convolution functions: All of the form "output = alpha * Op(inputs) + beta * output" */
    /** Function to perform the forward pass for batch convolution */
    public static int cudnnConvolutionForward(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnConvolutionForwardNative(handle, alpha, xDesc, x, wDesc, w, convDesc, algo, workSpace, workSpaceSizeInBytes, beta, yDesc, y));
    }
    private static native int cudnnConvolutionForwardNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Fused conv/bias/activation operation : y = Act( alpha1 * conv(x) + alpha2 * z + bias ) */
    public static int cudnnConvolutionBiasActivationForward(
        cudnnHandle handle, 
        Pointer alpha1, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer alpha2, 
        cudnnTensorDescriptor zDesc, 
        Pointer z, 
        cudnnTensorDescriptor biasDesc, 
        Pointer bias, 
        cudnnActivationDescriptor activationDesc, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnConvolutionBiasActivationForwardNative(handle, alpha1, xDesc, x, wDesc, w, convDesc, algo, workSpace, workSpaceSizeInBytes, alpha2, zDesc, z, biasDesc, bias, activationDesc, yDesc, y));
    }
    private static native int cudnnConvolutionBiasActivationForwardNative(
        cudnnHandle handle, 
        Pointer alpha1, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer alpha2, 
        cudnnTensorDescriptor zDesc, 
        Pointer z, 
        cudnnTensorDescriptor biasDesc, 
        Pointer bias, 
        cudnnActivationDescriptor activationDesc, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Function to compute the bias gradient for batch convolution */
    public static int cudnnConvolutionBackwardBias(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer beta, 
        cudnnTensorDescriptor dbDesc, 
        Pointer db)
    {
        return checkResult(cudnnConvolutionBackwardBiasNative(handle, alpha, dyDesc, dy, beta, dbDesc, db));
    }
    private static native int cudnnConvolutionBackwardBiasNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer beta, 
        cudnnTensorDescriptor dbDesc, 
        Pointer db);


    public static int cudnnGetConvolutionBackwardFilterAlgorithmMaxCount(
        cudnnHandle handle, 
        int[] count)
    {
        return checkResult(cudnnGetConvolutionBackwardFilterAlgorithmMaxCountNative(handle, count));
    }
    private static native int cudnnGetConvolutionBackwardFilterAlgorithmMaxCountNative(
        cudnnHandle handle, 
        int[] count);


    public static int cudnnFindConvolutionBackwardFilterAlgorithm(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults)
    {
        return checkResult(cudnnFindConvolutionBackwardFilterAlgorithmNative(handle, xDesc, dyDesc, convDesc, dwDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnFindConvolutionBackwardFilterAlgorithmNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults);


    public static int cudnnFindConvolutionBackwardFilterAlgorithmEx(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer y, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnFindConvolutionBackwardFilterAlgorithmExNative(handle, xDesc, x, dyDesc, y, convDesc, dwDesc, dw, requestedAlgoCount, returnedAlgoCount, perfResults, workSpace, workSpaceSizeInBytes));
    }
    private static native int cudnnFindConvolutionBackwardFilterAlgorithmExNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer y, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes);


    public static int cudnnGetConvolutionBackwardFilterAlgorithm(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo)
    {
        return checkResult(cudnnGetConvolutionBackwardFilterAlgorithmNative(handle, xDesc, dyDesc, convDesc, dwDesc, preference, memoryLimitInBytes, algo));
    }
    private static native int cudnnGetConvolutionBackwardFilterAlgorithmNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor dwDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo);


    public static int cudnnGetConvolutionBackwardFilterAlgorithm_v7(
        cudnnHandle handle, 
        cudnnTensorDescriptor srcDesc, 
        cudnnTensorDescriptor diffDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor gradDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults)
    {
        return checkResult(cudnnGetConvolutionBackwardFilterAlgorithm_v7Native(handle, srcDesc, diffDesc, convDesc, gradDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnGetConvolutionBackwardFilterAlgorithm_v7Native(
        cudnnHandle handle, 
        cudnnTensorDescriptor srcDesc, 
        cudnnTensorDescriptor diffDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor gradDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdFilterAlgoPerf[] perfResults);


    /**
     *  convolution algorithm (which requires potentially some workspace)
     */
    /** Helper function to return the minimum size of the workspace to be passed to the convolution given an algo*/
    public static int cudnnGetConvolutionBackwardFilterWorkspaceSize(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor gradDesc, 
        int algo, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetConvolutionBackwardFilterWorkspaceSizeNative(handle, xDesc, dyDesc, convDesc, gradDesc, algo, sizeInBytes));
    }
    private static native int cudnnGetConvolutionBackwardFilterWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnFilterDescriptor gradDesc, 
        int algo, 
        long[] sizeInBytes);


    public static int cudnnConvolutionBackwardFilter(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw)
    {
        return checkResult(cudnnConvolutionBackwardFilterNative(handle, alpha, xDesc, x, dyDesc, dy, convDesc, algo, workSpace, workSpaceSizeInBytes, beta, dwDesc, dw));
    }
    private static native int cudnnConvolutionBackwardFilterNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw);


    public static int cudnnGetConvolutionBackwardDataAlgorithmMaxCount(
        cudnnHandle handle, 
        int[] count)
    {
        return checkResult(cudnnGetConvolutionBackwardDataAlgorithmMaxCountNative(handle, count));
    }
    private static native int cudnnGetConvolutionBackwardDataAlgorithmMaxCountNative(
        cudnnHandle handle, 
        int[] count);


    public static int cudnnFindConvolutionBackwardDataAlgorithm(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults)
    {
        return checkResult(cudnnFindConvolutionBackwardDataAlgorithmNative(handle, wDesc, dyDesc, convDesc, dxDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnFindConvolutionBackwardDataAlgorithmNative(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults);


    public static int cudnnFindConvolutionBackwardDataAlgorithmEx(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnFindConvolutionBackwardDataAlgorithmExNative(handle, wDesc, w, dyDesc, dy, convDesc, dxDesc, dx, requestedAlgoCount, returnedAlgoCount, perfResults, workSpace, workSpaceSizeInBytes));
    }
    private static native int cudnnFindConvolutionBackwardDataAlgorithmExNative(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults, 
        Pointer workSpace, 
        long workSpaceSizeInBytes);


    public static int cudnnGetConvolutionBackwardDataAlgorithm(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo)
    {
        return checkResult(cudnnGetConvolutionBackwardDataAlgorithmNative(handle, wDesc, dyDesc, convDesc, dxDesc, preference, memoryLimitInBytes, algo));
    }
    private static native int cudnnGetConvolutionBackwardDataAlgorithmNative(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int preference, 
        long memoryLimitInBytes, 
        int[] algo);


    public static int cudnnGetConvolutionBackwardDataAlgorithm_v7(
        cudnnHandle handle, 
        cudnnFilterDescriptor filterDesc, 
        cudnnTensorDescriptor diffDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor gradDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults)
    {
        return checkResult(cudnnGetConvolutionBackwardDataAlgorithm_v7Native(handle, filterDesc, diffDesc, convDesc, gradDesc, requestedAlgoCount, returnedAlgoCount, perfResults));
    }
    private static native int cudnnGetConvolutionBackwardDataAlgorithm_v7Native(
        cudnnHandle handle, 
        cudnnFilterDescriptor filterDesc, 
        cudnnTensorDescriptor diffDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor gradDesc, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnConvolutionBwdDataAlgoPerf[] perfResults);


    /** Helper function to return the minimum size of the workspace to be passed to the convolution given an algo*/
    public static int cudnnGetConvolutionBackwardDataWorkspaceSize(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int algo, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetConvolutionBackwardDataWorkspaceSizeNative(handle, wDesc, dyDesc, convDesc, dxDesc, algo, sizeInBytes));
    }
    private static native int cudnnGetConvolutionBackwardDataWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnFilterDescriptor wDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnConvolutionDescriptor convDesc, 
        cudnnTensorDescriptor dxDesc, 
        int algo, 
        long[] sizeInBytes);


    public static int cudnnConvolutionBackwardData(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx)
    {
        return checkResult(cudnnConvolutionBackwardDataNative(handle, alpha, wDesc, w, dyDesc, dy, convDesc, algo, workSpace, workSpaceSizeInBytes, beta, dxDesc, dx));
    }
    private static native int cudnnConvolutionBackwardDataNative(
        cudnnHandle handle, 
        Pointer alpha, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnConvolutionDescriptor convDesc, 
        int algo, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx);


    public static int cudnnIm2Col(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        Pointer colBuffer)
    {
        return checkResult(cudnnIm2ColNative(handle, xDesc, x, wDesc, convDesc, colBuffer));
    }
    private static native int cudnnIm2ColNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        cudnnFilterDescriptor wDesc, 
        cudnnConvolutionDescriptor convDesc, 
        Pointer colBuffer);


    /** Softmax functions: All of the form "output = alpha * Op(inputs) + beta * output" */
    /** Function to perform forward softmax */
    public static int cudnnSoftmaxForward(
        cudnnHandle handle, 
        int algo, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnSoftmaxForwardNative(handle, algo, mode, alpha, xDesc, x, beta, yDesc, y));
    }
    private static native int cudnnSoftmaxForwardNative(
        cudnnHandle handle, 
        int algo, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Function to perform backward softmax */
    public static int cudnnSoftmaxBackward(
        cudnnHandle handle, 
        int algo, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx)
    {
        return checkResult(cudnnSoftmaxBackwardNative(handle, algo, mode, alpha, yDesc, y, dyDesc, dy, beta, dxDesc, dx));
    }
    private static native int cudnnSoftmaxBackwardNative(
        cudnnHandle handle, 
        int algo, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx);


    /** Create an instance of pooling descriptor */
    public static int cudnnCreatePoolingDescriptor(
        cudnnPoolingDescriptor poolingDesc)
    {
        return checkResult(cudnnCreatePoolingDescriptorNative(poolingDesc));
    }
    private static native int cudnnCreatePoolingDescriptorNative(
        cudnnPoolingDescriptor poolingDesc);


    public static int cudnnSetPooling2dDescriptor(
        cudnnPoolingDescriptor poolingDesc, 
        int mode, 
        int maxpoolingNanOpt, 
        int windowHeight, 
        int windowWidth, 
        int verticalPadding, 
        int horizontalPadding, 
        int verticalStride, 
        int horizontalStride)
    {
        return checkResult(cudnnSetPooling2dDescriptorNative(poolingDesc, mode, maxpoolingNanOpt, windowHeight, windowWidth, verticalPadding, horizontalPadding, verticalStride, horizontalStride));
    }
    private static native int cudnnSetPooling2dDescriptorNative(
        cudnnPoolingDescriptor poolingDesc, 
        int mode, 
        int maxpoolingNanOpt, 
        int windowHeight, 
        int windowWidth, 
        int verticalPadding, 
        int horizontalPadding, 
        int verticalStride, 
        int horizontalStride);


    public static int cudnnGetPooling2dDescriptor(
        cudnnPoolingDescriptor poolingDesc, 
        int[] mode, 
        int[] maxpoolingNanOpt, 
        int[] windowHeight, 
        int[] windowWidth, 
        int[] verticalPadding, 
        int[] horizontalPadding, 
        int[] verticalStride, 
        int[] horizontalStride)
    {
        return checkResult(cudnnGetPooling2dDescriptorNative(poolingDesc, mode, maxpoolingNanOpt, windowHeight, windowWidth, verticalPadding, horizontalPadding, verticalStride, horizontalStride));
    }
    private static native int cudnnGetPooling2dDescriptorNative(
        cudnnPoolingDescriptor poolingDesc, 
        int[] mode, 
        int[] maxpoolingNanOpt, 
        int[] windowHeight, 
        int[] windowWidth, 
        int[] verticalPadding, 
        int[] horizontalPadding, 
        int[] verticalStride, 
        int[] horizontalStride);


    public static int cudnnSetPoolingNdDescriptor(
        cudnnPoolingDescriptor poolingDesc, 
        int mode, 
        int maxpoolingNanOpt, 
        int nbDims, 
        int[] windowDimA, 
        int[] paddingA, 
        int[] strideA)
    {
        return checkResult(cudnnSetPoolingNdDescriptorNative(poolingDesc, mode, maxpoolingNanOpt, nbDims, windowDimA, paddingA, strideA));
    }
    private static native int cudnnSetPoolingNdDescriptorNative(
        cudnnPoolingDescriptor poolingDesc, 
        int mode, 
        int maxpoolingNanOpt, 
        int nbDims, 
        int[] windowDimA, 
        int[] paddingA, 
        int[] strideA);


    public static int cudnnGetPoolingNdDescriptor(
        cudnnPoolingDescriptor poolingDesc, 
        int nbDimsRequested, 
        int[] mode, 
        int[] maxpoolingNanOpt, 
        int[] nbDims, 
        int[] windowDimA, 
        int[] paddingA, 
        int[] strideA)
    {
        return checkResult(cudnnGetPoolingNdDescriptorNative(poolingDesc, nbDimsRequested, mode, maxpoolingNanOpt, nbDims, windowDimA, paddingA, strideA));
    }
    private static native int cudnnGetPoolingNdDescriptorNative(
        cudnnPoolingDescriptor poolingDesc, 
        int nbDimsRequested, 
        int[] mode, 
        int[] maxpoolingNanOpt, 
        int[] nbDims, 
        int[] windowDimA, 
        int[] paddingA, 
        int[] strideA);


    public static int cudnnGetPoolingNdForwardOutputDim(
        cudnnPoolingDescriptor poolingDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        int nbDims, 
        int[] outputTensorDimA)
    {
        return checkResult(cudnnGetPoolingNdForwardOutputDimNative(poolingDesc, inputTensorDesc, nbDims, outputTensorDimA));
    }
    private static native int cudnnGetPoolingNdForwardOutputDimNative(
        cudnnPoolingDescriptor poolingDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        int nbDims, 
        int[] outputTensorDimA);


    public static int cudnnGetPooling2dForwardOutputDim(
        cudnnPoolingDescriptor poolingDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        int[] n, 
        int[] c, 
        int[] h, 
        int[] w)
    {
        return checkResult(cudnnGetPooling2dForwardOutputDimNative(poolingDesc, inputTensorDesc, n, c, h, w));
    }
    private static native int cudnnGetPooling2dForwardOutputDimNative(
        cudnnPoolingDescriptor poolingDesc, 
        cudnnTensorDescriptor inputTensorDesc, 
        int[] n, 
        int[] c, 
        int[] h, 
        int[] w);


    /** Destroy an instance of pooling descriptor */
    public static int cudnnDestroyPoolingDescriptor(
        cudnnPoolingDescriptor poolingDesc)
    {
        return checkResult(cudnnDestroyPoolingDescriptorNative(poolingDesc));
    }
    private static native int cudnnDestroyPoolingDescriptorNative(
        cudnnPoolingDescriptor poolingDesc);


    /** Pooling functions: All of the form "output = alpha * Op(inputs) + beta * output" */
    /** Function to perform forward pooling */
    public static int cudnnPoolingForward(
        cudnnHandle handle, 
        cudnnPoolingDescriptor poolingDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnPoolingForwardNative(handle, poolingDesc, alpha, xDesc, x, beta, yDesc, y));
    }
    private static native int cudnnPoolingForwardNative(
        cudnnHandle handle, 
        cudnnPoolingDescriptor poolingDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Function to perform backward pooling */
    public static int cudnnPoolingBackward(
        cudnnHandle handle, 
        cudnnPoolingDescriptor poolingDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx)
    {
        return checkResult(cudnnPoolingBackwardNative(handle, poolingDesc, alpha, yDesc, y, dyDesc, dy, xDesc, x, beta, dxDesc, dx));
    }
    private static native int cudnnPoolingBackwardNative(
        cudnnHandle handle, 
        cudnnPoolingDescriptor poolingDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx);


    /** Activation functions: All of the form "output = alpha * Op(inputs) + beta * output" */
    public static int cudnnCreateActivationDescriptor(
        cudnnActivationDescriptor activationDesc)
    {
        return checkResult(cudnnCreateActivationDescriptorNative(activationDesc));
    }
    private static native int cudnnCreateActivationDescriptorNative(
        cudnnActivationDescriptor activationDesc);


    public static int cudnnSetActivationDescriptor(
        cudnnActivationDescriptor activationDesc, 
        int mode, 
        int reluNanOpt, 
        double coef)/** ceiling for clipped RELU, alpha for ELU */
    {
        return checkResult(cudnnSetActivationDescriptorNative(activationDesc, mode, reluNanOpt, coef));
    }
    private static native int cudnnSetActivationDescriptorNative(
        cudnnActivationDescriptor activationDesc, 
        int mode, 
        int reluNanOpt, 
        double coef);/** ceiling for clipped RELU, alpha for ELU */


    public static int cudnnGetActivationDescriptor(
        cudnnActivationDescriptor activationDesc, 
        int[] mode, 
        int[] reluNanOpt, 
        double[] coef)/** ceiling for clipped RELU, alpha for ELU */
    {
        return checkResult(cudnnGetActivationDescriptorNative(activationDesc, mode, reluNanOpt, coef));
    }
    private static native int cudnnGetActivationDescriptorNative(
        cudnnActivationDescriptor activationDesc, 
        int[] mode, 
        int[] reluNanOpt, 
        double[] coef);/** ceiling for clipped RELU, alpha for ELU */


    public static int cudnnDestroyActivationDescriptor(
        cudnnActivationDescriptor activationDesc)
    {
        return checkResult(cudnnDestroyActivationDescriptorNative(activationDesc));
    }
    private static native int cudnnDestroyActivationDescriptorNative(
        cudnnActivationDescriptor activationDesc);


    /** Function to perform forward activation  */
    public static int cudnnActivationForward(
        cudnnHandle handle, 
        cudnnActivationDescriptor activationDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnActivationForwardNative(handle, activationDesc, alpha, xDesc, x, beta, yDesc, y));
    }
    private static native int cudnnActivationForwardNative(
        cudnnHandle handle, 
        cudnnActivationDescriptor activationDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** Function to perform backward activation  */
    public static int cudnnActivationBackward(
        cudnnHandle handle, 
        cudnnActivationDescriptor activationDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx)
    {
        return checkResult(cudnnActivationBackwardNative(handle, activationDesc, alpha, yDesc, y, dyDesc, dy, xDesc, x, beta, dxDesc, dx));
    }
    private static native int cudnnActivationBackwardNative(
        cudnnHandle handle, 
        cudnnActivationDescriptor activationDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx);


    /**
     * <pre>
    * Create an instance of LRN (Local Response Normalization) descriptor
    * Uses lrnN=5, lrnAlpha=1e-4, lrnBeta=0.75, lrnK=2.0 as defaults from Krizhevsky'12 ImageNet paper
     * </pre>
     */
    public static int cudnnCreateLRNDescriptor(
        cudnnLRNDescriptor normDesc)
    {
        return checkResult(cudnnCreateLRNDescriptorNative(normDesc));
    }
    private static native int cudnnCreateLRNDescriptorNative(
        cudnnLRNDescriptor normDesc);


    /**
     * <pre>
    * Uses a window [center-lookBehind, center+lookAhead], where
    * lookBehind = floor( (lrnN-1)/2 ), lookAhead = lrnN-lookBehind-1.
    * Values of double parameters cast to tensor data type.
     * </pre>
     */
    public static int cudnnSetLRNDescriptor(
        cudnnLRNDescriptor normDesc, 
        int lrnN, 
        double lrnAlpha, 
        double lrnBeta, 
        double lrnK)
    {
        return checkResult(cudnnSetLRNDescriptorNative(normDesc, lrnN, lrnAlpha, lrnBeta, lrnK));
    }
    private static native int cudnnSetLRNDescriptorNative(
        cudnnLRNDescriptor normDesc, 
        int lrnN, 
        double lrnAlpha, 
        double lrnBeta, 
        double lrnK);


    /**
     * <pre>
    * Retrieve the settings currently stored in an LRN layer descriptor
    * Any of the provided pointers can be NULL (no corresponding value will be returned)
     * </pre>
     */
    public static int cudnnGetLRNDescriptor(
        cudnnLRNDescriptor normDesc, 
        int[] lrnN, 
        double[] lrnAlpha, 
        double[] lrnBeta, 
        double[] lrnK)
    {
        return checkResult(cudnnGetLRNDescriptorNative(normDesc, lrnN, lrnAlpha, lrnBeta, lrnK));
    }
    private static native int cudnnGetLRNDescriptorNative(
        cudnnLRNDescriptor normDesc, 
        int[] lrnN, 
        double[] lrnAlpha, 
        double[] lrnBeta, 
        double[] lrnK);


    /** Destroy an instance of LRN descriptor */
    public static int cudnnDestroyLRNDescriptor(
        cudnnLRNDescriptor lrnDesc)
    {
        return checkResult(cudnnDestroyLRNDescriptorNative(lrnDesc));
    }
    private static native int cudnnDestroyLRNDescriptorNative(
        cudnnLRNDescriptor lrnDesc);


    /** LRN functions: output = alpha * normalize(x) + beta * old_y */
    /** LRN cross-channel forward computation. Double parameters cast to tensor data type */
    public static int cudnnLRNCrossChannelForward(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int lrnMode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnLRNCrossChannelForwardNative(handle, normDesc, lrnMode, alpha, xDesc, x, beta, yDesc, y));
    }
    private static native int cudnnLRNCrossChannelForwardNative(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int lrnMode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    /** LRN cross-channel backward computation. Double parameters cast to tensor data type */
    public static int cudnnLRNCrossChannelBackward(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int lrnMode, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx)
    {
        return checkResult(cudnnLRNCrossChannelBackwardNative(handle, normDesc, lrnMode, alpha, yDesc, y, dyDesc, dy, xDesc, x, beta, dxDesc, dx));
    }
    private static native int cudnnLRNCrossChannelBackwardNative(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int lrnMode, 
        Pointer alpha, 
        cudnnTensorDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx);


    /** LCN/divisive normalization functions: y = alpha * normalize(x) + beta * y */
    public static int cudnnDivisiveNormalizationForward(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, /** same desc for means, temp, temp2 */
        Pointer x, 
        Pointer means, /** if NULL, means are assumed to be zero */
        Pointer temp, 
        Pointer temp2, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnDivisiveNormalizationForwardNative(handle, normDesc, mode, alpha, xDesc, x, means, temp, temp2, beta, yDesc, y));
    }
    private static native int cudnnDivisiveNormalizationForwardNative(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, /** same desc for means, temp, temp2 */
        Pointer x, 
        Pointer means, /** if NULL, means are assumed to be zero */
        Pointer temp, 
        Pointer temp2, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    public static int cudnnDivisiveNormalizationBackward(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, /** same desc for x, means, dy, temp, temp2 */
        Pointer x, 
        Pointer means, /** if NULL, means are assumed to be zero */
        Pointer dy, 
        Pointer temp, 
        Pointer temp2, 
        Pointer beta, 
        cudnnTensorDescriptor dXdMeansDesc, /** same desc for dx, dMeans */
        Pointer dx, /** output x differential */
        Pointer dMeans)/** output means differential, can be NULL */
    {
        return checkResult(cudnnDivisiveNormalizationBackwardNative(handle, normDesc, mode, alpha, xDesc, x, means, dy, temp, temp2, beta, dXdMeansDesc, dx, dMeans));
    }
    private static native int cudnnDivisiveNormalizationBackwardNative(
        cudnnHandle handle, 
        cudnnLRNDescriptor normDesc, 
        int mode, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, /** same desc for x, means, dy, temp, temp2 */
        Pointer x, 
        Pointer means, /** if NULL, means are assumed to be zero */
        Pointer dy, 
        Pointer temp, 
        Pointer temp2, 
        Pointer beta, 
        cudnnTensorDescriptor dXdMeansDesc, /** same desc for dx, dMeans */
        Pointer dx, /** output x differential */
        Pointer dMeans);/** output means differential, can be NULL */


    /**
     * <pre>
    * Derives a tensor descriptor from layer data descriptor for BatchNormalization
    * scale, invVariance, bnBias, bnScale tensors. Use this tensor desc for
    * bnScaleBiasMeanVarDesc and bnScaleBiasDiffDesc in Batch Normalization forward and backward functions.
     * </pre>
     */
    public static int cudnnDeriveBNTensorDescriptor(
        cudnnTensorDescriptor derivedBnDesc, 
        cudnnTensorDescriptor xDesc, 
        int mode)
    {
        return checkResult(cudnnDeriveBNTensorDescriptorNative(derivedBnDesc, xDesc, mode));
    }
    private static native int cudnnDeriveBNTensorDescriptorNative(
        cudnnTensorDescriptor derivedBnDesc, 
        cudnnTensorDescriptor xDesc, 
        int mode);


    public static int cudnnGetBatchNormalizationForwardTrainingExWorkspaceSize(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor zDesc, 
        cudnnTensorDescriptor yDesc, 
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        cudnnActivationDescriptor activationDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetBatchNormalizationForwardTrainingExWorkspaceSizeNative(handle, mode, bnOps, xDesc, zDesc, yDesc, bnScaleBiasMeanVarDesc, activationDesc, sizeInBytes));
    }
    private static native int cudnnGetBatchNormalizationForwardTrainingExWorkspaceSizeNative(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor zDesc, 
        cudnnTensorDescriptor yDesc, 
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        cudnnActivationDescriptor activationDesc, 
        long[] sizeInBytes);


    public static int cudnnGetBatchNormalizationBackwardExWorkspaceSize(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor yDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnTensorDescriptor dzDesc, 
        cudnnTensorDescriptor dxDesc, 
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        cudnnActivationDescriptor activationDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetBatchNormalizationBackwardExWorkspaceSizeNative(handle, mode, bnOps, xDesc, yDesc, dyDesc, dzDesc, dxDesc, dBnScaleBiasDesc, activationDesc, sizeInBytes));
    }
    private static native int cudnnGetBatchNormalizationBackwardExWorkspaceSizeNative(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnTensorDescriptor xDesc, 
        cudnnTensorDescriptor yDesc, 
        cudnnTensorDescriptor dyDesc, 
        cudnnTensorDescriptor dzDesc, 
        cudnnTensorDescriptor dxDesc, 
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        cudnnActivationDescriptor activationDesc, 
        long[] sizeInBytes);


    public static int cudnnGetBatchNormalizationTrainingExReserveSpaceSize(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnActivationDescriptor activationDesc, 
        cudnnTensorDescriptor xDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetBatchNormalizationTrainingExReserveSpaceSizeNative(handle, mode, bnOps, activationDesc, xDesc, sizeInBytes));
    }
    private static native int cudnnGetBatchNormalizationTrainingExReserveSpaceSizeNative(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        cudnnActivationDescriptor activationDesc, 
        cudnnTensorDescriptor xDesc, 
        long[] sizeInBytes);


    /** Computes y = BN(x). Also accumulates moving averages of mean and inverse variances */
    public static int cudnnBatchNormalizationForwardTraining(
        cudnnHandle handle, 
        int mode, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer x, /** NxCxHxW */
        cudnnTensorDescriptor yDesc, 
        Pointer y, /** NxCxHxW */
        /**
         * <pre>
         * Shared desc for the next 6 tensors in the argument list.
               Data type to be set as follows:
               type = (typeOf(x) == double) ? double : float
               Dimensions for this descriptor depend on normalization mode
               - Spatial Normalization : tensors are expected to have dims 1xCx1x1
                (normalization is performed across NxHxW)
               - Per-Activation Normalization : tensors are expected to have dims of 1xCxHxW
         * (normalization is performed across N)
         * </pre>
         */
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        /** 'Gamma' and 'Beta' respectively in Ioffe and Szegedy's paper's notation */
        Pointer bnScale, 
        Pointer bnBias, 
        /**
         * <pre>
         * MUST use factor=1 in the very first call of a complete training cycle.
               Use a factor=1/(1+n) at N-th call to the function to get
               Cumulative Moving Average (CMA) behavior
               CMA[n] = (x[1]+...+x[n])/n
               Since CMA[n+1] = (n*CMA[n]+x[n+1])/(n+1) =
               ((n+1)*CMA[n]-CMA[n])/(n+1) + x[n+1]/(n+1) =
         * CMA[n]*(1-1/(n+1)) + x[n+1]*1/(n+1)
         * </pre>
         */
        double exponentialAverageFactor, 
        /** Used in Training phase only.
               runningMean = newMean*factor + runningMean*(1-factor) */
        Pointer resultRunningMean, 
        /** Output in training mode, input in inference. Is the moving average
               of  variance[x] (factor is applied in the same way as for runningMean) */
        Pointer resultRunningVariance, 
        /** Has to be >= CUDNN_BN_MIN_EPSILON. Should be the same in forward and backward functions. */
        double epsilon, 
        /** Optionally save intermediate results from the forward pass here
               - can be reused to speed up backward pass. NULL if unused */
        Pointer resultSaveMean, 
        Pointer resultSaveInvVariance)
    {
        return checkResult(cudnnBatchNormalizationForwardTrainingNative(handle, mode, alpha, beta, xDesc, x, yDesc, y, bnScaleBiasMeanVarDesc, bnScale, bnBias, exponentialAverageFactor, resultRunningMean, resultRunningVariance, epsilon, resultSaveMean, resultSaveInvVariance));
    }
    private static native int cudnnBatchNormalizationForwardTrainingNative(
        cudnnHandle handle, 
        int mode, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer x, /** NxCxHxW */
        cudnnTensorDescriptor yDesc, 
        Pointer y, /** NxCxHxW */
        /**
         * <pre>
         * Shared desc for the next 6 tensors in the argument list.
               Data type to be set as follows:
               type = (typeOf(x) == double) ? double : float
               Dimensions for this descriptor depend on normalization mode
               - Spatial Normalization : tensors are expected to have dims 1xCx1x1
                (normalization is performed across NxHxW)
               - Per-Activation Normalization : tensors are expected to have dims of 1xCxHxW
         * (normalization is performed across N)
         * </pre>
         */
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        /** 'Gamma' and 'Beta' respectively in Ioffe and Szegedy's paper's notation */
        Pointer bnScale, 
        Pointer bnBias, 
        /**
         * <pre>
         * MUST use factor=1 in the very first call of a complete training cycle.
               Use a factor=1/(1+n) at N-th call to the function to get
               Cumulative Moving Average (CMA) behavior
               CMA[n] = (x[1]+...+x[n])/n
               Since CMA[n+1] = (n*CMA[n]+x[n+1])/(n+1) =
               ((n+1)*CMA[n]-CMA[n])/(n+1) + x[n+1]/(n+1) =
         * CMA[n]*(1-1/(n+1)) + x[n+1]*1/(n+1)
         * </pre>
         */
        double exponentialAverageFactor, 
        /** Used in Training phase only.
               runningMean = newMean*factor + runningMean*(1-factor) */
        Pointer resultRunningMean, 
        /** Output in training mode, input in inference. Is the moving average
               of  variance[x] (factor is applied in the same way as for runningMean) */
        Pointer resultRunningVariance, 
        /** Has to be >= CUDNN_BN_MIN_EPSILON. Should be the same in forward and backward functions. */
        double epsilon, 
        /** Optionally save intermediate results from the forward pass here
               - can be reused to speed up backward pass. NULL if unused */
        Pointer resultSaveMean, 
        Pointer resultSaveInvVariance);


    /** Computes y = relu(BN(x) + z). Also accumulates moving averages of mean and inverse variances */
    public static int cudnnBatchNormalizationForwardTrainingEx(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer xData, 
        cudnnTensorDescriptor zDesc, 
        Pointer zData, 
        cudnnTensorDescriptor yDesc, 
        Pointer yData, 
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        Pointer bnScale, 
        Pointer bnBias, 
        double exponentialAverageFactor, 
        Pointer resultRunningMean, 
        Pointer resultRunningVariance, 
        /** Has to be >= CUDNN_BN_MIN_EPSILON. Should be the same in forward and backward functions. */
        double epsilon, 
        /** Optionally save intermediate results from the forward pass here
               - can be reused to speed up backward pass. NULL if unused */
        Pointer resultSaveMean, 
        Pointer resultSaveInvVariance, 
        cudnnActivationDescriptor activationDesc, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnBatchNormalizationForwardTrainingExNative(handle, mode, bnOps, alpha, beta, xDesc, xData, zDesc, zData, yDesc, yData, bnScaleBiasMeanVarDesc, bnScale, bnBias, exponentialAverageFactor, resultRunningMean, resultRunningVariance, epsilon, resultSaveMean, resultSaveInvVariance, activationDesc, workspace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnBatchNormalizationForwardTrainingExNative(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer xData, 
        cudnnTensorDescriptor zDesc, 
        Pointer zData, 
        cudnnTensorDescriptor yDesc, 
        Pointer yData, 
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        Pointer bnScale, 
        Pointer bnBias, 
        double exponentialAverageFactor, 
        Pointer resultRunningMean, 
        Pointer resultRunningVariance, 
        /** Has to be >= CUDNN_BN_MIN_EPSILON. Should be the same in forward and backward functions. */
        double epsilon, 
        /** Optionally save intermediate results from the forward pass here
               - can be reused to speed up backward pass. NULL if unused */
        Pointer resultSaveMean, 
        Pointer resultSaveInvVariance, 
        cudnnActivationDescriptor activationDesc, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    /**
     * <pre>
    * Performs Batch Normalization during Inference:
    * y[i] = bnScale[k]*(x[i]-estimatedMean[k])/sqrt(epsilon+estimatedVariance[k]) + bnBias[k]
    * with bnScale, bnBias, runningMean, runningInvVariance tensors indexed
    * according to spatial or per-activation mode. Refer to cudnnBatchNormalizationForwardTraining
    * above for notes on function arguments.
     * </pre>
     */
    public static int cudnnBatchNormalizationForwardInference(
        cudnnHandle handle, 
        int mode, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer x, /** NxCxHxW */
        cudnnTensorDescriptor yDesc, 
        Pointer y, /** NxCxHxW */
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        Pointer bnScale, 
        Pointer bnBias, 
        Pointer estimatedMean, 
        Pointer estimatedVariance, 
        double epsilon)
    {
        return checkResult(cudnnBatchNormalizationForwardInferenceNative(handle, mode, alpha, beta, xDesc, x, yDesc, y, bnScaleBiasMeanVarDesc, bnScale, bnBias, estimatedMean, estimatedVariance, epsilon));
    }
    private static native int cudnnBatchNormalizationForwardInferenceNative(
        cudnnHandle handle, 
        int mode, 
        Pointer alpha, /** alpha[0] = result blend factor */
        Pointer beta, /** beta[0] = dest layer blend factor */
        cudnnTensorDescriptor xDesc, 
        Pointer x, /** NxCxHxW */
        cudnnTensorDescriptor yDesc, 
        Pointer y, /** NxCxHxW */
        cudnnTensorDescriptor bnScaleBiasMeanVarDesc, 
        Pointer bnScale, 
        Pointer bnBias, 
        Pointer estimatedMean, 
        Pointer estimatedVariance, 
        double epsilon);


    /** Performs backward pass of Batch Normalization layer. Returns x gradient,
    * bnScale gradient and bnBias gradient */
    public static int cudnnBatchNormalizationBackward(
        cudnnHandle handle, 
        int mode, 
        Pointer alphaDataDiff, 
        Pointer betaDataDiff, 
        Pointer alphaParamDiff, 
        Pointer betaParamDiff, 
        cudnnTensorDescriptor xDesc, /** same desc for x, dx, dy */
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        /** Shared tensor desc for the 4 tensors below */
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        Pointer bnScale, /** bnBias doesn't affect backpropagation */
        /** scale and bias diff are not backpropagated below this layer */
        Pointer dBnScaleResult, 
        Pointer dBnBiasResult, 
        /** Same epsilon as forward pass */
        double epsilon, 
        /** Optionally cached intermediate results from
                                           forward pass */
        Pointer savedMean, 
        Pointer savedInvVariance)
    {
        return checkResult(cudnnBatchNormalizationBackwardNative(handle, mode, alphaDataDiff, betaDataDiff, alphaParamDiff, betaParamDiff, xDesc, x, dyDesc, dy, dxDesc, dx, dBnScaleBiasDesc, bnScale, dBnScaleResult, dBnBiasResult, epsilon, savedMean, savedInvVariance));
    }
    private static native int cudnnBatchNormalizationBackwardNative(
        cudnnHandle handle, 
        int mode, 
        Pointer alphaDataDiff, 
        Pointer betaDataDiff, 
        Pointer alphaParamDiff, 
        Pointer betaParamDiff, 
        cudnnTensorDescriptor xDesc, /** same desc for x, dx, dy */
        Pointer x, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        /** Shared tensor desc for the 4 tensors below */
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        Pointer bnScale, /** bnBias doesn't affect backpropagation */
        /** scale and bias diff are not backpropagated below this layer */
        Pointer dBnScaleResult, 
        Pointer dBnBiasResult, 
        /** Same epsilon as forward pass */
        double epsilon, 
        /** Optionally cached intermediate results from
                                           forward pass */
        Pointer savedMean, 
        Pointer savedInvVariance);


    public static int cudnnBatchNormalizationBackwardEx(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        Pointer alphaDataDiff, 
        Pointer betaDataDiff, 
        Pointer alphaParamDiff, 
        Pointer betaParamDiff, 
        cudnnTensorDescriptor xDesc, 
        Pointer xData, 
        cudnnTensorDescriptor yDesc, 
        Pointer yData, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dyData, 
        cudnnTensorDescriptor dzDesc, 
        Pointer dzData, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dxData, 
        /** Shared tensor desc for the 4 tensors below */
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        Pointer bnScaleData, 
        Pointer bnBiasData, /** needed if there is activation */
        Pointer dBnScaleData, 
        Pointer dBnBiasData, 
        double epsilon, /** Same epsilon as forward pass */
        /** Optionally cached intermediate results from
                                             forward pass */
        Pointer savedMean, 
        Pointer savedInvVariance, 
        cudnnActivationDescriptor activationDesc, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnBatchNormalizationBackwardExNative(handle, mode, bnOps, alphaDataDiff, betaDataDiff, alphaParamDiff, betaParamDiff, xDesc, xData, yDesc, yData, dyDesc, dyData, dzDesc, dzData, dxDesc, dxData, dBnScaleBiasDesc, bnScaleData, bnBiasData, dBnScaleData, dBnBiasData, epsilon, savedMean, savedInvVariance, activationDesc, workSpace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnBatchNormalizationBackwardExNative(
        cudnnHandle handle, 
        int mode, 
        int bnOps, 
        Pointer alphaDataDiff, 
        Pointer betaDataDiff, 
        Pointer alphaParamDiff, 
        Pointer betaParamDiff, 
        cudnnTensorDescriptor xDesc, 
        Pointer xData, 
        cudnnTensorDescriptor yDesc, 
        Pointer yData, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dyData, 
        cudnnTensorDescriptor dzDesc, 
        Pointer dzData, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dxData, 
        /** Shared tensor desc for the 4 tensors below */
        cudnnTensorDescriptor dBnScaleBiasDesc, 
        Pointer bnScaleData, 
        Pointer bnBiasData, /** needed if there is activation */
        Pointer dBnScaleData, 
        Pointer dBnBiasData, 
        double epsilon, /** Same epsilon as forward pass */
        /** Optionally cached intermediate results from
                                             forward pass */
        Pointer savedMean, 
        Pointer savedInvVariance, 
        cudnnActivationDescriptor activationDesc, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnCreateSpatialTransformerDescriptor(
        cudnnSpatialTransformerDescriptor stDesc)
    {
        return checkResult(cudnnCreateSpatialTransformerDescriptorNative(stDesc));
    }
    private static native int cudnnCreateSpatialTransformerDescriptorNative(
        cudnnSpatialTransformerDescriptor stDesc);


    public static int cudnnSetSpatialTransformerNdDescriptor(
        cudnnSpatialTransformerDescriptor stDesc, 
        int samplerType, 
        int dataType, 
        int nbDims, 
        int[] dimA)
    {
        return checkResult(cudnnSetSpatialTransformerNdDescriptorNative(stDesc, samplerType, dataType, nbDims, dimA));
    }
    private static native int cudnnSetSpatialTransformerNdDescriptorNative(
        cudnnSpatialTransformerDescriptor stDesc, 
        int samplerType, 
        int dataType, 
        int nbDims, 
        int[] dimA);


    public static int cudnnDestroySpatialTransformerDescriptor(
        cudnnSpatialTransformerDescriptor stDesc)
    {
        return checkResult(cudnnDestroySpatialTransformerDescriptorNative(stDesc));
    }
    private static native int cudnnDestroySpatialTransformerDescriptorNative(
        cudnnSpatialTransformerDescriptor stDesc);


    public static int cudnnSpatialTfGridGeneratorForward(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer theta, 
        Pointer grid)
    {
        return checkResult(cudnnSpatialTfGridGeneratorForwardNative(handle, stDesc, theta, grid));
    }
    private static native int cudnnSpatialTfGridGeneratorForwardNative(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer theta, 
        Pointer grid);


    public static int cudnnSpatialTfGridGeneratorBackward(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer dgrid, 
        Pointer dtheta)
    {
        return checkResult(cudnnSpatialTfGridGeneratorBackwardNative(handle, stDesc, dgrid, dtheta));
    }
    private static native int cudnnSpatialTfGridGeneratorBackwardNative(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer dgrid, 
        Pointer dtheta);


    public static int cudnnSpatialTfSamplerForward(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer grid, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y)
    {
        return checkResult(cudnnSpatialTfSamplerForwardNative(handle, stDesc, alpha, xDesc, x, grid, beta, yDesc, y));
    }
    private static native int cudnnSpatialTfSamplerForwardNative(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer grid, 
        Pointer beta, 
        cudnnTensorDescriptor yDesc, 
        Pointer y);


    public static int cudnnSpatialTfSamplerBackward(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        Pointer alphaDgrid, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer grid, 
        Pointer betaDgrid, 
        Pointer dgrid)
    {
        return checkResult(cudnnSpatialTfSamplerBackwardNative(handle, stDesc, alpha, xDesc, x, beta, dxDesc, dx, alphaDgrid, dyDesc, dy, grid, betaDgrid, dgrid));
    }
    private static native int cudnnSpatialTfSamplerBackwardNative(
        cudnnHandle handle, 
        cudnnSpatialTransformerDescriptor stDesc, 
        Pointer alpha, 
        cudnnTensorDescriptor xDesc, 
        Pointer x, 
        Pointer beta, 
        cudnnTensorDescriptor dxDesc, 
        Pointer dx, 
        Pointer alphaDgrid, 
        cudnnTensorDescriptor dyDesc, 
        Pointer dy, 
        Pointer grid, 
        Pointer betaDgrid, 
        Pointer dgrid);


    public static int cudnnCreateDropoutDescriptor(
        cudnnDropoutDescriptor dropoutDesc)
    {
        return checkResult(cudnnCreateDropoutDescriptorNative(dropoutDesc));
    }
    private static native int cudnnCreateDropoutDescriptorNative(
        cudnnDropoutDescriptor dropoutDesc);


    public static int cudnnDestroyDropoutDescriptor(
        cudnnDropoutDescriptor dropoutDesc)
    {
        return checkResult(cudnnDestroyDropoutDescriptorNative(dropoutDesc));
    }
    private static native int cudnnDestroyDropoutDescriptorNative(
        cudnnDropoutDescriptor dropoutDesc);


    /**helper function to determine size of the states to be passed to cudnnSetDropoutDescriptor */
    public static int cudnnDropoutGetStatesSize(
        cudnnHandle handle, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnDropoutGetStatesSizeNative(handle, sizeInBytes));
    }
    private static native int cudnnDropoutGetStatesSizeNative(
        cudnnHandle handle, 
        long[] sizeInBytes);


    /**helper function to determine size of the reserve space to be passed to dropout forward/backward calls */
    public static int cudnnDropoutGetReserveSpaceSize(
        cudnnTensorDescriptor xdesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnDropoutGetReserveSpaceSizeNative(xdesc, sizeInBytes));
    }
    private static native int cudnnDropoutGetReserveSpaceSizeNative(
        cudnnTensorDescriptor xdesc, 
        long[] sizeInBytes);


    public static int cudnnSetDropoutDescriptor(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float dropout, 
        Pointer states, 
        long stateSizeInBytes, 
        long seed)
    {
        return checkResult(cudnnSetDropoutDescriptorNative(dropoutDesc, handle, dropout, states, stateSizeInBytes, seed));
    }
    private static native int cudnnSetDropoutDescriptorNative(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float dropout, 
        Pointer states, 
        long stateSizeInBytes, 
        long seed);


    /** Restores the dropout descriptor to a previously saved-off state */
    public static int cudnnRestoreDropoutDescriptor(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float dropout, 
        Pointer states, 
        long stateSizeInBytes, 
        long seed)
    {
        return checkResult(cudnnRestoreDropoutDescriptorNative(dropoutDesc, handle, dropout, states, stateSizeInBytes, seed));
    }
    private static native int cudnnRestoreDropoutDescriptorNative(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float dropout, 
        Pointer states, 
        long stateSizeInBytes, 
        long seed);


    public static int cudnnGetDropoutDescriptor(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float[] dropout, 
        Pointer states, 
        long[] seed)
    {
        return checkResult(cudnnGetDropoutDescriptorNative(dropoutDesc, handle, dropout, states, seed));
    }
    private static native int cudnnGetDropoutDescriptorNative(
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnHandle handle, 
        float[] dropout, 
        Pointer states, 
        long[] seed);


    public static int cudnnDropoutForward(
        cudnnHandle handle, 
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnTensorDescriptor xdesc, 
        Pointer x, 
        cudnnTensorDescriptor ydesc, 
        Pointer y, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnDropoutForwardNative(handle, dropoutDesc, xdesc, x, ydesc, y, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnDropoutForwardNative(
        cudnnHandle handle, 
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnTensorDescriptor xdesc, 
        Pointer x, 
        cudnnTensorDescriptor ydesc, 
        Pointer y, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnDropoutBackward(
        cudnnHandle handle, 
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnTensorDescriptor dydesc, 
        Pointer dy, 
        cudnnTensorDescriptor dxdesc, 
        Pointer dx, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnDropoutBackwardNative(handle, dropoutDesc, dydesc, dy, dxdesc, dx, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnDropoutBackwardNative(
        cudnnHandle handle, 
        cudnnDropoutDescriptor dropoutDesc, 
        cudnnTensorDescriptor dydesc, 
        Pointer dy, 
        cudnnTensorDescriptor dxdesc, 
        Pointer dx, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnCreateRNNDescriptor(
        cudnnRNNDescriptor rnnDesc)
    {
        return checkResult(cudnnCreateRNNDescriptorNative(rnnDesc));
    }
    private static native int cudnnCreateRNNDescriptorNative(
        cudnnRNNDescriptor rnnDesc);


    public static int cudnnDestroyRNNDescriptor(
        cudnnRNNDescriptor rnnDesc)
    {
        return checkResult(cudnnDestroyRNNDescriptorNative(rnnDesc));
    }
    private static native int cudnnDestroyRNNDescriptorNative(
        cudnnRNNDescriptor rnnDesc);


    public static int cudnnGetRNNForwardInferenceAlgorithmMaxCount(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count)
    {
        return checkResult(cudnnGetRNNForwardInferenceAlgorithmMaxCountNative(handle, rnnDesc, count));
    }
    private static native int cudnnGetRNNForwardInferenceAlgorithmMaxCountNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count);


    public static int cudnnFindRNNForwardInferenceAlgorithmEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnFindRNNForwardInferenceAlgorithmExNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, findIntensity, requestedAlgoCount, returnedAlgoCount, perfResults, workspace, workSpaceSizeInBytes));
    }
    private static native int cudnnFindRNNForwardInferenceAlgorithmExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes);


    public static int cudnnGetRNNForwardTrainingAlgorithmMaxCount(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count)
    {
        return checkResult(cudnnGetRNNForwardTrainingAlgorithmMaxCountNative(handle, rnnDesc, count));
    }
    private static native int cudnnGetRNNForwardTrainingAlgorithmMaxCountNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count);


    public static int cudnnFindRNNForwardTrainingAlgorithmEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnFindRNNForwardTrainingAlgorithmExNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, findIntensity, requestedAlgoCount, returnedAlgoCount, perfResults, workspace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnFindRNNForwardTrainingAlgorithmExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnGetRNNBackwardDataAlgorithmMaxCount(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count)
    {
        return checkResult(cudnnGetRNNBackwardDataAlgorithmMaxCountNative(handle, rnnDesc, count));
    }
    private static native int cudnnGetRNNBackwardDataAlgorithmMaxCountNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count);


    public static int cudnnFindRNNBackwardDataAlgorithmEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor[] dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnTensorDescriptor[] dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnFindRNNBackwardDataAlgorithmExNative(handle, rnnDesc, seqLength, yDesc, y, dyDesc, dy, dhyDesc, dhy, dcyDesc, dcy, wDesc, w, hxDesc, hx, cxDesc, cx, dxDesc, dx, dhxDesc, dhx, dcxDesc, dcx, findIntensity, requestedAlgoCount, returnedAlgoCount, perfResults, workspace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnFindRNNBackwardDataAlgorithmExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor[] dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnTensorDescriptor[] dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnGetRNNBackwardWeightsAlgorithmMaxCount(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count)
    {
        return checkResult(cudnnGetRNNBackwardWeightsAlgorithmMaxCountNative(handle, rnnDesc, count));
    }
    private static native int cudnnGetRNNBackwardWeightsAlgorithmMaxCountNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] count);


    public static int cudnnFindRNNBackwardWeightsAlgorithmEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnFindRNNBackwardWeightsAlgorithmExNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, yDesc, y, findIntensity, requestedAlgoCount, returnedAlgoCount, perfResults, workspace, workSpaceSizeInBytes, dwDesc, dw, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnFindRNNBackwardWeightsAlgorithmExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        float findIntensity, 
        int requestedAlgoCount, 
        int[] returnedAlgoCount, 
        cudnnAlgorithmPerformance[] perfResults, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    /** Expensive. Creates the plan for the specific settings. */
    public static int cudnnCreatePersistentRNNPlan(
        cudnnRNNDescriptor rnnDesc, 
        int minibatch, 
        int dataType, 
        cudnnPersistentRNNPlan plan)
    {
        return checkResult(cudnnCreatePersistentRNNPlanNative(rnnDesc, minibatch, dataType, plan));
    }
    private static native int cudnnCreatePersistentRNNPlanNative(
        cudnnRNNDescriptor rnnDesc, 
        int minibatch, 
        int dataType, 
        cudnnPersistentRNNPlan plan);


    /** Attaches the plan to the descriptor. */
    public static int cudnnSetPersistentRNNPlan(
        cudnnRNNDescriptor rnnDesc, 
        cudnnPersistentRNNPlan plan)
    {
        return checkResult(cudnnSetPersistentRNNPlanNative(rnnDesc, plan));
    }
    private static native int cudnnSetPersistentRNNPlanNative(
        cudnnRNNDescriptor rnnDesc, 
        cudnnPersistentRNNPlan plan);


    public static int cudnnDestroyPersistentRNNPlan(
        cudnnPersistentRNNPlan plan)
    {
        return checkResult(cudnnDestroyPersistentRNNPlanNative(plan));
    }
    private static native int cudnnDestroyPersistentRNNPlanNative(
        cudnnPersistentRNNPlan plan);


    public static int cudnnSetRNNDescriptor(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, /** Between layers, not between recurrent steps. */
        int inputMode, 
        int direction, 
        int mode, 
        int algo, 
        int dataType)
    {
        return checkResult(cudnnSetRNNDescriptorNative(handle, rnnDesc, hiddenSize, numLayers, dropoutDesc, inputMode, direction, mode, algo, dataType));
    }
    private static native int cudnnSetRNNDescriptorNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, /** Between layers, not between recurrent steps. */
        int inputMode, 
        int direction, 
        int mode, 
        int algo, 
        int dataType);


    public static int cudnnSetRNNProjectionLayers(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int recProjSize, 
        int outProjSize)
    {
        return checkResult(cudnnSetRNNProjectionLayersNative(handle, rnnDesc, recProjSize, outProjSize));
    }
    private static native int cudnnSetRNNProjectionLayersNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int recProjSize, 
        int outProjSize);


    public static int cudnnGetRNNProjectionLayers(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] recProjSize, 
        int[] outProjSize)
    {
        return checkResult(cudnnGetRNNProjectionLayersNative(handle, rnnDesc, recProjSize, outProjSize));
    }
    private static native int cudnnGetRNNProjectionLayersNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] recProjSize, 
        int[] outProjSize);


    public static int cudnnSetRNNAlgorithmDescriptor(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnAlgorithmDescriptor algoDesc)
    {
        return checkResult(cudnnSetRNNAlgorithmDescriptorNative(handle, rnnDesc, algoDesc));
    }
    private static native int cudnnSetRNNAlgorithmDescriptorNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnAlgorithmDescriptor algoDesc);


    public static int cudnnGetRNNDescriptor(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] hiddenSize, 
        int[] numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int[] inputMode, 
        int[] direction, 
        int[] mode, 
        int[] algo, 
        int[] dataType)
    {
        return checkResult(cudnnGetRNNDescriptorNative(handle, rnnDesc, hiddenSize, numLayers, dropoutDesc, inputMode, direction, mode, algo, dataType));
    }
    private static native int cudnnGetRNNDescriptorNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] hiddenSize, 
        int[] numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int[] inputMode, 
        int[] direction, 
        int[] mode, 
        int[] algo, 
        int[] dataType);


    public static int cudnnSetRNNMatrixMathType(
        cudnnRNNDescriptor rnnDesc, 
        int mType)
    {
        return checkResult(cudnnSetRNNMatrixMathTypeNative(rnnDesc, mType));
    }
    private static native int cudnnSetRNNMatrixMathTypeNative(
        cudnnRNNDescriptor rnnDesc, 
        int mType);


    public static int cudnnGetRNNMatrixMathType(
        cudnnRNNDescriptor rnnDesc, 
        int[] mType)
    {
        return checkResult(cudnnGetRNNMatrixMathTypeNative(rnnDesc, mType));
    }
    private static native int cudnnGetRNNMatrixMathTypeNative(
        cudnnRNNDescriptor rnnDesc, 
        int[] mType);


    /** dataType in the RNN descriptor is used to determine math precision */
    /** dataType in weight descriptors and input descriptors is used to describe storage */
    public static int cudnnGetRNNWorkspaceSize(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetRNNWorkspaceSizeNative(handle, rnnDesc, seqLength, xDesc, sizeInBytes));
    }
    private static native int cudnnGetRNNWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        long[] sizeInBytes);


    public static int cudnnGetRNNTrainingReserveSize(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        long[] sizeInBytes)
    {
        return checkResult(cudnnGetRNNTrainingReserveSizeNative(handle, rnnDesc, seqLength, xDesc, sizeInBytes));
    }
    private static native int cudnnGetRNNTrainingReserveSizeNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        long[] sizeInBytes);


    public static int cudnnGetRNNParamsSize(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnTensorDescriptor xDesc, 
        long[] sizeInBytes, 
        int dataType)
    {
        return checkResult(cudnnGetRNNParamsSizeNative(handle, rnnDesc, xDesc, sizeInBytes, dataType));
    }
    private static native int cudnnGetRNNParamsSizeNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnTensorDescriptor xDesc, 
        long[] sizeInBytes, 
        int dataType);


    public static int cudnnGetRNNLinLayerMatrixParams(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int pseudoLayer, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        int linLayerID, 
        cudnnFilterDescriptor linLayerMatDesc, 
        Pointer linLayerMat)
    {
        return checkResult(cudnnGetRNNLinLayerMatrixParamsNative(handle, rnnDesc, pseudoLayer, xDesc, wDesc, w, linLayerID, linLayerMatDesc, linLayerMat));
    }
    private static native int cudnnGetRNNLinLayerMatrixParamsNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int pseudoLayer, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        int linLayerID, 
        cudnnFilterDescriptor linLayerMatDesc, 
        Pointer linLayerMat);


    public static int cudnnGetRNNLinLayerBiasParams(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int pseudoLayer, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        int linLayerID, 
        cudnnFilterDescriptor linLayerBiasDesc, 
        Pointer linLayerBias)
    {
        return checkResult(cudnnGetRNNLinLayerBiasParamsNative(handle, rnnDesc, pseudoLayer, xDesc, wDesc, w, linLayerID, linLayerBiasDesc, linLayerBias));
    }
    private static native int cudnnGetRNNLinLayerBiasParamsNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int pseudoLayer, 
        cudnnTensorDescriptor xDesc, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        int linLayerID, 
        cudnnFilterDescriptor linLayerBiasDesc, 
        Pointer linLayerBias);


    public static int cudnnRNNForwardInference(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        Pointer workspace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNForwardInferenceNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, workspace, workSpaceSizeInBytes));
    }
    private static native int cudnnRNNForwardInferenceNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        Pointer workspace, 
        long workSpaceSizeInBytes);


    public static int cudnnRNNForwardTraining(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNForwardTrainingNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, workspace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNForwardTrainingNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnRNNBackwardData(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor[] dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnTensorDescriptor[] dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNBackwardDataNative(handle, rnnDesc, seqLength, yDesc, y, dyDesc, dy, dhyDesc, dhy, dcyDesc, dcy, wDesc, w, hxDesc, hx, cxDesc, cx, dxDesc, dx, dhxDesc, dhx, dcxDesc, dcx, workspace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNBackwardDataNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        cudnnTensorDescriptor[] dyDesc, 
        Pointer dy, 
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnTensorDescriptor[] dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnRNNBackwardWeights(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNBackwardWeightsNative(handle, rnnDesc, seqLength, xDesc, x, hxDesc, hx, yDesc, y, workspace, workSpaceSizeInBytes, dwDesc, dw, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNBackwardWeightsNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int seqLength, 
        cudnnTensorDescriptor[] xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor[] yDesc, 
        Pointer y, 
        Pointer workspace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    /**
    * Create an instance of a CTC (Connectionist Temporal Classification) loss descriptor
    */
    public static int cudnnCreateCTCLossDescriptor(
        cudnnCTCLossDescriptor ctcLossDesc)
    {
        return checkResult(cudnnCreateCTCLossDescriptorNative(ctcLossDesc));
    }
    private static native int cudnnCreateCTCLossDescriptorNative(
        cudnnCTCLossDescriptor ctcLossDesc);


    public static int cudnnSetCTCLossDescriptor(
        cudnnCTCLossDescriptor ctcLossDesc, 
        int compType)
    {
        return checkResult(cudnnSetCTCLossDescriptorNative(ctcLossDesc, compType));
    }
    private static native int cudnnSetCTCLossDescriptorNative(
        cudnnCTCLossDescriptor ctcLossDesc, 
        int compType);


    public static int cudnnGetCTCLossDescriptor(
        cudnnCTCLossDescriptor ctcLossDesc, 
        int[] compType)
    {
        return checkResult(cudnnGetCTCLossDescriptorNative(ctcLossDesc, compType));
    }
    private static native int cudnnGetCTCLossDescriptorNative(
        cudnnCTCLossDescriptor ctcLossDesc, 
        int[] compType);


    public static int cudnnDestroyCTCLossDescriptor(
        cudnnCTCLossDescriptor ctcLossDesc)
    {
        return checkResult(cudnnDestroyCTCLossDescriptorNative(ctcLossDesc));
    }
    private static native int cudnnDestroyCTCLossDescriptorNative(
        cudnnCTCLossDescriptor ctcLossDesc);


    /** return the ctc costs and gradients, given the probabilities and labels */
    public static int cudnnCTCLoss(
        cudnnHandle handle, 
        cudnnTensorDescriptor probsDesc, /** Tensor descriptor for probabilities, the dimensions are T,N,A (T is the timing steps, N is the
                          mini batch size, A is the alphabet size)  */
        Pointer probs, /** probabilities after softmax, in GPU memory */
        int[] labels, /** labels, in CPU memory */
        int[] labelLengths, /** the length of each label, in CPU memory */
        int[] inputLengths, /** the lengths of timing steps in each batch, in CPU memory */
        Pointer costs, /** the returned costs of CTC, in GPU memory */
        cudnnTensorDescriptor gradientsDesc, /** Tensor descriptor for gradients, the dimensions are T,N,A */
        Pointer gradients, /** the returned CTC gradients, in GPU memory, to compute costs only, set it to NULL */
        int algo, /** algorithm selected, supported now 0 and 1 */
        cudnnCTCLossDescriptor ctcLossDesc, 
        Pointer workspace, /** pointer to the workspace, in GPU memory */
        long workSpaceSizeInBytes)/** the workspace size needed */
    {
        return checkResult(cudnnCTCLossNative(handle, probsDesc, probs, labels, labelLengths, inputLengths, costs, gradientsDesc, gradients, algo, ctcLossDesc, workspace, workSpaceSizeInBytes));
    }
    private static native int cudnnCTCLossNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor probsDesc, /** Tensor descriptor for probabilities, the dimensions are T,N,A (T is the timing steps, N is the
                          mini batch size, A is the alphabet size)  */
        Pointer probs, /** probabilities after softmax, in GPU memory */
        int[] labels, /** labels, in CPU memory */
        int[] labelLengths, /** the length of each label, in CPU memory */
        int[] inputLengths, /** the lengths of timing steps in each batch, in CPU memory */
        Pointer costs, /** the returned costs of CTC, in GPU memory */
        cudnnTensorDescriptor gradientsDesc, /** Tensor descriptor for gradients, the dimensions are T,N,A */
        Pointer gradients, /** the returned CTC gradients, in GPU memory, to compute costs only, set it to NULL */
        int algo, /** algorithm selected, supported now 0 and 1 */
        cudnnCTCLossDescriptor ctcLossDesc, 
        Pointer workspace, /** pointer to the workspace, in GPU memory */
        long workSpaceSizeInBytes);/** the workspace size needed */


    /** return the workspace size needed for ctc */
    public static int cudnnGetCTCLossWorkspaceSize(
        cudnnHandle handle, 
        cudnnTensorDescriptor probsDesc, /** Tensor descriptor for probabilities, the dimensions are T,N,A (T is the
                                                timing steps, N is the mini batch size, A is the alphabet size) */
        cudnnTensorDescriptor gradientsDesc, /** Tensor descriptor for gradients, the
                                                    dimensions are T,N,A. To compute costs
                                                    only, set it to NULL */
        int[] labels, /** labels, in CPU memory */
        int[] labelLengths, /** the length of each label, in CPU memory */
        int[] inputLengths, /** the lengths of timing steps in each batch, in CPU memory */
        int algo, /** algorithm selected, supported now 0 and 1 */
        cudnnCTCLossDescriptor ctcLossDesc, 
        long[] sizeInBytes)/** pointer to the returned workspace size */
    {
        return checkResult(cudnnGetCTCLossWorkspaceSizeNative(handle, probsDesc, gradientsDesc, labels, labelLengths, inputLengths, algo, ctcLossDesc, sizeInBytes));
    }
    private static native int cudnnGetCTCLossWorkspaceSizeNative(
        cudnnHandle handle, 
        cudnnTensorDescriptor probsDesc, /** Tensor descriptor for probabilities, the dimensions are T,N,A (T is the
                                                timing steps, N is the mini batch size, A is the alphabet size) */
        cudnnTensorDescriptor gradientsDesc, /** Tensor descriptor for gradients, the
                                                    dimensions are T,N,A. To compute costs
                                                    only, set it to NULL */
        int[] labels, /** labels, in CPU memory */
        int[] labelLengths, /** the length of each label, in CPU memory */
        int[] inputLengths, /** the lengths of timing steps in each batch, in CPU memory */
        int algo, /** algorithm selected, supported now 0 and 1 */
        cudnnCTCLossDescriptor ctcLossDesc, 
        long[] sizeInBytes);/** pointer to the returned workspace size */


    public static int cudnnCreateAlgorithmDescriptor(
        cudnnAlgorithmDescriptor algoDesc)
    {
        return checkResult(cudnnCreateAlgorithmDescriptorNative(algoDesc));
    }
    private static native int cudnnCreateAlgorithmDescriptorNative(
        cudnnAlgorithmDescriptor algoDesc);


    public static int cudnnSetAlgorithmDescriptor(
        cudnnAlgorithmDescriptor algoDesc, 
        int algorithm)
    {
        return checkResult(cudnnSetAlgorithmDescriptorNative(algoDesc, algorithm));
    }
    private static native int cudnnSetAlgorithmDescriptorNative(
        cudnnAlgorithmDescriptor algoDesc, 
        int algorithm);


    public static int cudnnGetAlgorithmDescriptor(
        cudnnAlgorithmDescriptor algoDesc, 
        int[] algorithm)
    {
        return checkResult(cudnnGetAlgorithmDescriptorNative(algoDesc, algorithm));
    }
    private static native int cudnnGetAlgorithmDescriptorNative(
        cudnnAlgorithmDescriptor algoDesc, 
        int[] algorithm);


    public static int cudnnCopyAlgorithmDescriptor(
        cudnnAlgorithmDescriptor src, 
        cudnnAlgorithmDescriptor dest)
    {
        return checkResult(cudnnCopyAlgorithmDescriptorNative(src, dest));
    }
    private static native int cudnnCopyAlgorithmDescriptorNative(
        cudnnAlgorithmDescriptor src, 
        cudnnAlgorithmDescriptor dest);


    public static int cudnnDestroyAlgorithmDescriptor(
        cudnnAlgorithmDescriptor algoDesc)
    {
        return checkResult(cudnnDestroyAlgorithmDescriptorNative(algoDesc));
    }
    private static native int cudnnDestroyAlgorithmDescriptorNative(
        cudnnAlgorithmDescriptor algoDesc);


    public static int cudnnCreateAlgorithmPerformance(
        cudnnAlgorithmPerformance[] algoPerf, 
        int numberToCreate)
    {
        return checkResult(cudnnCreateAlgorithmPerformanceNative(algoPerf, numberToCreate));
    }
    private static native int cudnnCreateAlgorithmPerformanceNative(
        cudnnAlgorithmPerformance[] algoPerf, 
        int numberToCreate);


    public static int cudnnSetAlgorithmPerformance(
        cudnnAlgorithmPerformance algoPerf, 
        cudnnAlgorithmDescriptor algoDesc, 
        int status, 
        float time, 
        long memory)
    {
        return checkResult(cudnnSetAlgorithmPerformanceNative(algoPerf, algoDesc, status, time, memory));
    }
    private static native int cudnnSetAlgorithmPerformanceNative(
        cudnnAlgorithmPerformance algoPerf, 
        cudnnAlgorithmDescriptor algoDesc, 
        int status, 
        float time, 
        long memory);


    public static int cudnnGetAlgorithmPerformance(
        cudnnAlgorithmPerformance algoPerf, 
        cudnnAlgorithmDescriptor algoDesc, 
        int[] status, 
        float[] time, 
        long[] memory)
    {
        return checkResult(cudnnGetAlgorithmPerformanceNative(algoPerf, algoDesc, status, time, memory));
    }
    private static native int cudnnGetAlgorithmPerformanceNative(
        cudnnAlgorithmPerformance algoPerf, 
        cudnnAlgorithmDescriptor algoDesc, 
        int[] status, 
        float[] time, 
        long[] memory);


    public static int cudnnDestroyAlgorithmPerformance(
        cudnnAlgorithmPerformance[] algoPerf, 
        int numberToDestroy)
    {
        return checkResult(cudnnDestroyAlgorithmPerformanceNative(algoPerf, numberToDestroy));
    }
    private static native int cudnnDestroyAlgorithmPerformanceNative(
        cudnnAlgorithmPerformance[] algoPerf, 
        int numberToDestroy);


    public static int cudnnGetAlgorithmSpaceSize(
        cudnnHandle handle, 
        cudnnAlgorithmDescriptor algoDesc, 
        long[] algoSpaceSizeInBytes)
    {
        return checkResult(cudnnGetAlgorithmSpaceSizeNative(handle, algoDesc, algoSpaceSizeInBytes));
    }
    private static native int cudnnGetAlgorithmSpaceSizeNative(
        cudnnHandle handle, 
        cudnnAlgorithmDescriptor algoDesc, 
        long[] algoSpaceSizeInBytes);


    public static int cudnnSaveAlgorithm(
        cudnnHandle handle, 
        cudnnAlgorithmDescriptor algoDesc, 
        Pointer algoSpace, 
        long algoSpaceSizeInBytes)
    {
        return checkResult(cudnnSaveAlgorithmNative(handle, algoDesc, algoSpace, algoSpaceSizeInBytes));
    }
    private static native int cudnnSaveAlgorithmNative(
        cudnnHandle handle, 
        cudnnAlgorithmDescriptor algoDesc, 
        Pointer algoSpace, 
        long algoSpaceSizeInBytes);


    public static int cudnnRestoreAlgorithm(
        cudnnHandle handle, 
        Pointer algoSpace, 
        long algoSpaceSizeInBytes, 
        cudnnAlgorithmDescriptor algoDesc)
    {
        return checkResult(cudnnRestoreAlgorithmNative(handle, algoSpace, algoSpaceSizeInBytes, algoDesc));
    }
    private static native int cudnnRestoreAlgorithmNative(
        cudnnHandle handle, 
        Pointer algoSpace, 
        long algoSpaceSizeInBytes, 
        cudnnAlgorithmDescriptor algoDesc);


    public static int cudnnRNNSetClip(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int clipMode, 
        int clipNanOpt, 
        double lclip, 
        double rclip)
    {
        return checkResult(cudnnRNNSetClipNative(handle, rnnDesc, clipMode, clipNanOpt, lclip, rclip));
    }
    private static native int cudnnRNNSetClipNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int clipMode, 
        int clipNanOpt, 
        double lclip, 
        double rclip);


    public static int cudnnRNNGetClip(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] clipMode, 
        int[] clipNanOpt, 
        double[] lclip, 
        double[] rclip)
    {
        return checkResult(cudnnRNNGetClipNative(handle, rnnDesc, clipMode, clipNanOpt, lclip, rclip));
    }
    private static native int cudnnRNNGetClipNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int[] clipMode, 
        int[] clipNanOpt, 
        double[] lclip, 
        double[] rclip);


    public static int cudnnSetCallback(
        int mask, 
        Object udata, 
        cudnnCallback fptr)
    {
        return checkResult(cudnnSetCallbackNative(mask, udata, fptr));
    }
    private static native int cudnnSetCallbackNative(
        int mask, 
        Object udata, 
        cudnnCallback fptr);


    public static int cudnnGetCallback(
        int[] mask, 
        Object udata, 
        cudnnCallback[] fptr)
    {
        return checkResult(cudnnGetCallbackNative(mask, udata, fptr));
    }
    private static native int cudnnGetCallbackNative(
        int[] mask, 
        Object udata, 
        cudnnCallback[] fptr);


    public static int cudnnSetRNNPaddingMode(
        cudnnRNNDescriptor rnnDesc, 
        int paddingMode)
    {
        return checkResult(cudnnSetRNNPaddingModeNative(rnnDesc, paddingMode));
    }
    private static native int cudnnSetRNNPaddingModeNative(
        cudnnRNNDescriptor rnnDesc, 
        int paddingMode);


    public static int cudnnGetRNNPaddingMode(
        cudnnRNNDescriptor rnnDesc, 
        int[] paddingMode)
    {
        return checkResult(cudnnGetRNNPaddingModeNative(rnnDesc, paddingMode));
    }
    private static native int cudnnGetRNNPaddingModeNative(
        cudnnRNNDescriptor rnnDesc, 
        int[] paddingMode);


    public static int cudnnCreateRNNDataDescriptor(
        cudnnRNNDataDescriptor RNNDataDesc)
    {
        return checkResult(cudnnCreateRNNDataDescriptorNative(RNNDataDesc));
    }
    private static native int cudnnCreateRNNDataDescriptorNative(
        cudnnRNNDataDescriptor RNNDataDesc);


    public static int cudnnDestroyRNNDataDescriptor(
        cudnnRNNDataDescriptor RNNDataDesc)
    {
        return checkResult(cudnnDestroyRNNDataDescriptorNative(RNNDataDesc));
    }
    private static native int cudnnDestroyRNNDataDescriptorNative(
        cudnnRNNDataDescriptor RNNDataDesc);


    public static int cudnnSetRNNDataDescriptor(
        cudnnRNNDataDescriptor RNNDataDesc, 
        int dataType, 
        int layout, 
        int maxSeqLength, 
        int batchSize, 
        int vectorSize, 
        int[] seqLengthArray, /** length of each sequence in the batch */
        Pointer paddingFill)/** symbol for filling padding position in output */
    {
        return checkResult(cudnnSetRNNDataDescriptorNative(RNNDataDesc, dataType, layout, maxSeqLength, batchSize, vectorSize, seqLengthArray, paddingFill));
    }
    private static native int cudnnSetRNNDataDescriptorNative(
        cudnnRNNDataDescriptor RNNDataDesc, 
        int dataType, 
        int layout, 
        int maxSeqLength, 
        int batchSize, 
        int vectorSize, 
        int[] seqLengthArray, /** length of each sequence in the batch */
        Pointer paddingFill);/** symbol for filling padding position in output */


    public static int cudnnGetRNNDataDescriptor(
        cudnnRNNDataDescriptor RNNDataDesc, 
        int[] dataType, 
        int[] layout, 
        int[] maxSeqLength, 
        int[] batchSize, 
        int[] vectorSize, 
        int arrayLengthRequested, 
        int[] seqLengthArray, 
        Pointer paddingFill)
    {
        return checkResult(cudnnGetRNNDataDescriptorNative(RNNDataDesc, dataType, layout, maxSeqLength, batchSize, vectorSize, arrayLengthRequested, seqLengthArray, paddingFill));
    }
    private static native int cudnnGetRNNDataDescriptorNative(
        cudnnRNNDataDescriptor RNNDataDesc, 
        int[] dataType, 
        int[] layout, 
        int[] maxSeqLength, 
        int[] batchSize, 
        int[] vectorSize, 
        int arrayLengthRequested, 
        int[] seqLengthArray, 
        Pointer paddingFill);


    public static int cudnnRNNForwardTrainingEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        cudnnRNNDataDescriptor kDesc, /** reserved, should pass NULL */
        Pointer keys, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor cDesc, /** reserved, should pass NULL */
        Pointer cAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor iDesc, /** reserved, should pass NULL */
        Pointer iAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor qDesc, /** reserved, should pass NULL */
        Pointer queries, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNForwardTrainingExNative(handle, rnnDesc, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, kDesc, keys, cDesc, cAttn, iDesc, iAttn, qDesc, queries, workSpace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNForwardTrainingExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        cudnnRNNDataDescriptor kDesc, /** reserved, should pass NULL */
        Pointer keys, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor cDesc, /** reserved, should pass NULL */
        Pointer cAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor iDesc, /** reserved, should pass NULL */
        Pointer iAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor qDesc, /** reserved, should pass NULL */
        Pointer queries, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnRNNForwardInferenceEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        cudnnRNNDataDescriptor kDesc, /** reserved, should pass NULL */
        Pointer keys, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor cDesc, /** reserved, should pass NULL */
        Pointer cAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor iDesc, /** reserved, should pass NULL */
        Pointer iAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor qDesc, /** reserved, should pass NULL */
        Pointer queries, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNForwardInferenceExNative(handle, rnnDesc, xDesc, x, hxDesc, hx, cxDesc, cx, wDesc, w, yDesc, y, hyDesc, hy, cyDesc, cy, kDesc, keys, cDesc, cAttn, iDesc, iAttn, qDesc, queries, workSpace, workSpaceSizeInBytes));
    }
    private static native int cudnnRNNForwardInferenceExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnTensorDescriptor hyDesc, 
        Pointer hy, 
        cudnnTensorDescriptor cyDesc, 
        Pointer cy, 
        cudnnRNNDataDescriptor kDesc, /** reserved, should pass NULL */
        Pointer keys, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor cDesc, /** reserved, should pass NULL */
        Pointer cAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor iDesc, /** reserved, should pass NULL */
        Pointer iAttn, /** reserved, should pass NULL */
        cudnnRNNDataDescriptor qDesc, /** reserved, should pass NULL */
        Pointer queries, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes);


    public static int cudnnRNNBackwardDataEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnRNNDataDescriptor dyDesc, 
        Pointer dy, 
        cudnnRNNDataDescriptor dcDesc, /** reserved, should pass NULL */
        Pointer dcAttn, /** reserved, should pass NULL */
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnRNNDataDescriptor dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        cudnnRNNDataDescriptor dkDesc, /** reserved, should pass NULL */
        Pointer dkeys, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNBackwardDataExNative(handle, rnnDesc, yDesc, y, dyDesc, dy, dcDesc, dcAttn, dhyDesc, dhy, dcyDesc, dcy, wDesc, w, hxDesc, hx, cxDesc, cx, dxDesc, dx, dhxDesc, dhx, dcxDesc, dcx, dkDesc, dkeys, workSpace, workSpaceSizeInBytes, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNBackwardDataExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        cudnnRNNDataDescriptor dyDesc, 
        Pointer dy, 
        cudnnRNNDataDescriptor dcDesc, /** reserved, should pass NULL */
        Pointer dcAttn, /** reserved, should pass NULL */
        cudnnTensorDescriptor dhyDesc, 
        Pointer dhy, 
        cudnnTensorDescriptor dcyDesc, 
        Pointer dcy, 
        cudnnFilterDescriptor wDesc, 
        Pointer w, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnTensorDescriptor cxDesc, 
        Pointer cx, 
        cudnnRNNDataDescriptor dxDesc, 
        Pointer dx, 
        cudnnTensorDescriptor dhxDesc, 
        Pointer dhx, 
        cudnnTensorDescriptor dcxDesc, 
        Pointer dcx, 
        cudnnRNNDataDescriptor dkDesc, /** reserved, should pass NULL */
        Pointer dkeys, /** reserved, should pass NULL */
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    public static int cudnnRNNBackwardWeightsEx(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes)
    {
        return checkResult(cudnnRNNBackwardWeightsExNative(handle, rnnDesc, xDesc, x, hxDesc, hx, yDesc, y, workSpace, workSpaceSizeInBytes, dwDesc, dw, reserveSpace, reserveSpaceSizeInBytes));
    }
    private static native int cudnnRNNBackwardWeightsExNative(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        cudnnRNNDataDescriptor xDesc, 
        Pointer x, 
        cudnnTensorDescriptor hxDesc, 
        Pointer hx, 
        cudnnRNNDataDescriptor yDesc, 
        Pointer y, 
        Pointer workSpace, 
        long workSpaceSizeInBytes, 
        cudnnFilterDescriptor dwDesc, 
        Pointer dw, 
        Pointer reserveSpace, 
        long reserveSpaceSizeInBytes);


    /**
     * <pre>
     * DEPRECATED routines to be removed next release :
       User should use the non-suffixed version (which has the API and functionality of _v6 version)
       Routines with _v5 suffix has the functionality of the non-suffixed routines in the CUDNN V6
     * </pre>
     */
    public static int cudnnSetRNNDescriptor_v6(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int inputMode, 
        int direction, 
        int mode, 
        int algo, 
        int dataType)
    {
        return checkResult(cudnnSetRNNDescriptor_v6Native(handle, rnnDesc, hiddenSize, numLayers, dropoutDesc, inputMode, direction, mode, algo, dataType));
    }
    private static native int cudnnSetRNNDescriptor_v6Native(
        cudnnHandle handle, 
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int inputMode, 
        int direction, 
        int mode, 
        int algo, 
        int dataType);


    public static int cudnnSetRNNDescriptor_v5(
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int inputMode, 
        int direction, 
        int mode, 
        int dataType)
    {
        return checkResult(cudnnSetRNNDescriptor_v5Native(rnnDesc, hiddenSize, numLayers, dropoutDesc, inputMode, direction, mode, dataType));
    }
    private static native int cudnnSetRNNDescriptor_v5Native(
        cudnnRNNDescriptor rnnDesc, 
        int hiddenSize, 
        int numLayers, 
        cudnnDropoutDescriptor dropoutDesc, 
        int inputMode, 
        int direction, 
        int mode, 
        int dataType);


}

