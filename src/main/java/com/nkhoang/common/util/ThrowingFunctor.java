package com.nkhoang.common.util;


/**
 * VoidFunctor, Functor.  If you feel the need to make a Functor2, or
 * FunctorN, or VoidFunctorN, try this instead:
 * <p/>
 * new Functor<ReturnType,Tuple<Type1,Type2>>() {
 * public ReturnType execute( Tuple<Type1,Type2> ) {
 * ...
 * }
 * }
 */
public interface ThrowingFunctor<ReturnType, ArgType, ExceptionType extends Throwable> {
	public ReturnType execute(ArgType arg) throws ExceptionType;
}
