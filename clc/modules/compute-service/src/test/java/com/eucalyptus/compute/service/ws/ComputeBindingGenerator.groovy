/*************************************************************************
 * Copyright 2017 Ent. Services Development Corporation LP
 *
 * Redistribution and use of this software in source and binary forms,
 * with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************/
package com.eucalyptus.compute.service.ws

import com.eucalyptus.compute.common.ComputeMessage
import com.eucalyptus.compute.common.IamInstanceProfileComputeMessage
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import org.junit.Ignore
import org.junit.Test
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

/**
 * Developer test that can be used to generate bindings from message classes for compute.
 */
@Ignore("Developer test")
class ComputeBindingGenerator {

  @Test
  void generate() {
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider( false )
    scanner.addIncludeFilter( new AssignableTypeFilter( IamInstanceProfileComputeMessage ) );
    Set<String> messageClasses = Sets.newTreeSet( scanner.findCandidateComponents( ComputeMessage.package.name )*.beanClassName )
    messageClasses.removeAll( [ 'com.eucalyptus.compute.common.IamInstanceProfileComputeMessage' ] )

    Set<String> beanClasses = Sets.newTreeSet( )
    Set<String> printedBeanClasses = Sets.newHashSet( )
    for ( String messageclass : messageClasses ) {
      String simpleName = messageclass.substring( messageclass.lastIndexOf( '.' ) + 1 )

      if ( messageclass.endsWith( "Type" ) ) {
        println """  <mapping name="${simpleName.substring(0,simpleName.length()-4)}" class="${messageclass}" extends="com.eucalyptus.compute.common.ComputeMessage">"""
        println """    <structure map-as="com.eucalyptus.compute.common.ComputeMessage"/>"""
        printFields( messageclass, beanClasses )
        println """  </mapping>"""
      } else {
        throw new Exception( "Unexpected message class : ${messageclass}" )
      }
    }

    while ( !printedBeanClasses.containsAll( beanClasses ) ) {
      for ( String beanClass : Lists.newArrayList( beanClasses ) ) {
        if ( printedBeanClasses.contains( beanClass ) ) continue;
        println """  <mapping class="${beanClass}" abstract="true">"""
        printFields( beanClass, beanClasses )
        println """  </mapping>"""
        printedBeanClasses << beanClass
      }
    }
  }

  void printFields( String messageclass, Collection<String> beanClasses ) {
    Class.forName( messageclass ).declaredFields.each { Field field ->
      if ( field.name.startsWith( '$' ) || field.name.startsWith( '_' ) || field.type.package.name.startsWith( 'groovy.lang' ) ) {
        // ignore
      } else if ( Collection.class.isAssignableFrom( field.type ) ) {
        Class itemType = ( Class ) ( ( ParameterizedType ) field.genericType ).actualTypeArguments[0];
        if ( itemType.name.startsWith( 'java' ) ) {
          println """    <collection name="${field.name.substring(0,1).toUpperCase()}${field.name.substring(1)}" field="${field.name}" usage="optional">"""
          println """      <value name="member" type="${itemType.name}"/>"""
          println """    </collection>"""
        } else {
          println """    <collection name="${field.name.substring(0,1).toUpperCase()}${field.name.substring(1)}" field="${field.name}" usage="optional">>"""
          println """      <structure name="member" type="${itemType.name}"/>"""
          println """    </collection>"""
          beanClasses << itemType.name
        }
      } else if ( field.type.package.name.startsWith( 'java' ) ) {
        println """    <value name="${field.name.substring(0,1).toUpperCase()}${field.name.substring(1)}" field="${field.name}" usage="optional"/>"""
      } else {
        println """    <structure name="${field.name.substring(0,1).toUpperCase()}${field.name.substring(1)}" field="${field.name}" usage="optional" type="${field.type.name}"/>"""
        beanClasses << field.type.name
      }
    }
  }
}
